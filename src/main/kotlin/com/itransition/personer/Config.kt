package com.itransition.personer

import com.natpryce.konfig.ConfigurationProperties
import com.natpryce.konfig.EnvironmentVariables
import com.natpryce.konfig.getValue
import com.natpryce.konfig.intType
import com.natpryce.konfig.overriding
import com.natpryce.konfig.stringType
import java.io.File

val PERSONER_JIRA_URL by stringType
val PERSONER_JIRA_USERNAME by stringType
val PERSONER_JIRA_PASSWORD by stringType
val PERSONER_JIRA_PROJECT by stringType
val PERSONER_JIRA_CUSTOMER_REGION_FIELD by stringType
val PERSONER_JIRA_PERSONAL_DATA_REGION_FIELD by stringType
val PERSONER_PAGE_SIZE by intType
val env = EnvironmentVariables() overriding ConfigurationProperties.fromOptionalFile(File(".env"))
