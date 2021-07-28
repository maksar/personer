package com.itransition.personer

import Region.None
import com.atlassian.jira.rest.client.api.domain.input.ComplexIssueInputFieldValue
import com.atlassian.jira.rest.client.api.domain.input.FieldInput
import com.atlassian.jira.rest.client.api.domain.input.IssueInput.createWithFields
import kotlinx.coroutines.runBlocking
import org.codehaus.jettison.json.JSONObject

const val VALUE: String = "value"
const val ID: String = "id"

fun main() {
    println(env[PERSONER_COUNTRIES_CONFIG])
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
                env[PERSONER_COUNTRIES_CONFIG].getOrDefault(region, None),
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
