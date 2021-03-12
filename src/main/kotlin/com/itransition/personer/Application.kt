package com.itransition.personer

import com.atlassian.jira.rest.client.api.domain.input.ComplexIssueInputFieldValue
import com.atlassian.jira.rest.client.api.domain.input.FieldInput
import com.atlassian.jira.rest.client.api.domain.input.IssueInput.createWithFields
import com.itransition.personer.Region.*
import kotlinx.coroutines.FlowPreview
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
    "Austria", "Germany", "Switzerland", "Netherlands", "Sweden", "Luxembourg", "Belgium", "Italy", "Norway", "Estonia",
    "Denmark", "Spain", "Czech Republic", "Portugal", "Latvia", "Cyprus", "Finland", "Gibraltar", "Greece",
    "Poland", "Montenegro", "Austria", "Slovak Republic", "Hungary", "France"
    ),
    None {
        override fun id(): String = "-1"
    };

    open fun id(): String = possibleValues.first { it.value == name }.id.toString()

    companion object {
        fun fromRegion(region: String?): Region = values().firstOrNull { it.names.contains(region) } ?: None
    }
}

@FlowPreview
suspend fun main() {
    projectCards(setOf(
        env[PERSONER_JIRA_CUSTOMER_REGION_FIELD],
        env[PERSONER_JIRA_PERSONAL_DATA_REGION_FIELD]
    )).associateWith {
        Pair(
            getField(it, PERSONER_JIRA_CUSTOMER_REGION_FIELD)?.toString()?.split(", ")?.first(),
            (getField(it, PERSONER_JIRA_PERSONAL_DATA_REGION_FIELD) as JSONObject?)?.getString(VALUE)
        )
    }.filterValues { (region, _) ->
        region != null
    }.mapValues { (_, regions) ->
        Triple(regions.first, Region.fromRegion(regions.first), regions.second)
    }.also {
        it.values.asSequence().filter { it.second == None }.map { it.first }
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
