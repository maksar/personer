package com.itransition.personer

import com.atlassian.jira.rest.client.api.domain.input.ComplexIssueInputFieldValue
import com.atlassian.jira.rest.client.api.domain.input.FieldInput
import com.atlassian.jira.rest.client.api.domain.input.IssueInput.createWithFields
import com.itransition.personer.Region.*
import com.itransition.personer.Region.Companion.fromRegion
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.runBlocking
import org.codehaus.jettison.json.JSONObject

const val VALUE: String = "value"
const val ID: String = "id"

val project = jiraClient.projectClient.getProject(env[PERSONER_JIRA_PROJECT]).get()
val possibleValues = (jiraClient.issueClient as MyAsynchronousIssueRestClient)
    .getAllowedValues(project).getValue(env[PERSONER_JIRA_PERSONAL_DATA_REGION_FIELD])


enum class Region(vararg val names: String) {
    USA("United States"),
    UK("Ireland", "United Kingdom"),
    CIS("Belarus", "Russian Federation", "Kazakhstan", "Ukraine"),
    EU(
        "Austria", "Belgium", "Cyprus", "Czech Republic", "Denmark", "Estonia", "Finland", "France", "Germany",
        "Gibraltar", "Greece", "Hungary", "Italy", "Latvia", "Luxembourg", "Montenegro", "Netherlands", "Norway",
        "Poland", "Portugal", "Slovak Republic", "Spain", "Sweden", "Switzerland"
    ),
    None {
        override fun id(): String = "-1"
    };

    open fun id(): String = possibleValues.first { it.value == name }.id.toString()

    companion object {
        fun fromRegion(region: String): Region = values().firstOrNull { it.names.contains(region) } ?: None
    }
}

fun main() {
    runBlocking {
        projectCards(
            setOf(
                env[PERSONER_JIRA_CUSTOMER_REGION_FIELD],
                env[PERSONER_JIRA_PERSONAL_DATA_REGION_FIELD]
            )
        )
    }.associateWith {
        (getField(it, PERSONER_JIRA_CUSTOMER_REGION_FIELD)?.toString()?.split(", ")?.first() ?: None.name).let { region ->
            Triple(
                region,
                fromRegion(region),
                (getField(it, PERSONER_JIRA_PERSONAL_DATA_REGION_FIELD) as JSONObject?)?.getString(VALUE) ?: None.name
            )
        }
    }.also {
        it.values.asSequence().filter { it.first != None.name && it.second == None }.map { it.first }
            .distinct().sortedBy { it }.toList().takeIf { it.isNotEmpty() }?.let {
                println("Following country regions are not mapped:")
                it.forEach(::println)
            }
    }.filterValues { (_, region, dataRegion) ->
        region.name != dataRegion
    }.mapValues { (_, regions) ->
        regions.second
    }.forEach { (issue, value) ->
        jiraClient.issueClient.updateIssue(
            issue.key,
            createWithFields(
                FieldInput(
                    env[PERSONER_JIRA_PERSONAL_DATA_REGION_FIELD],
                    ComplexIssueInputFieldValue(mapOf(VALUE to value.name, ID to value.id())))
            )
        ).get()
    }
}
