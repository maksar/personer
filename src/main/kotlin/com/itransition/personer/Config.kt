package com.itransition.personer

import com.natpryce.konfig.*
import java.io.File

val PERSONER_JIRA_URL by stringType
val PERSONER_JIRA_USERNAME by stringType
val PERSONER_JIRA_PASSWORD by stringType
val PERSONER_JIRA_PROJECT by stringType
val PERSONER_JIRA_CUSTOMER_REGION_FIELD by stringType
val PERSONER_JIRA_PERSONAL_DATA_REGION_FIELD by stringType
val PERSONER_PAGE_SIZE by intType
val PERSONER_COUNTRIES_CONFIG by
    listType(
        listType(
            stringType,
            ":".toRegex()
        ).wrappedAs { mapping -> mapping.last().split(",").map { Pair(it, Region.valueOf(mapping.first())) } },
        ";".toRegex()).wrappedAs { it.flatten().toMap() }
val env = EnvironmentVariables() overriding ConfigurationProperties.fromOptionalFile(File(".env"))