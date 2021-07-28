import com.itransition.personer.*
import com.itransition.personer.jiraClient

val project = jiraClient.projectClient.getProject(env[PERSONER_JIRA_PROJECT]).get()
val possibleValues = (jiraClient.issueClient as MyAsynchronousIssueRestClient)
    .getAllowedValues(project).getValue(env[PERSONER_JIRA_PERSONAL_DATA_REGION_FIELD])

enum class Region {
    USA,
    UK,
    CIS,
    EU,
    None {
        override fun id(): String = "-1"
    };

    open fun id(): String = possibleValues.first { it.value == name }.id.toString()
}
