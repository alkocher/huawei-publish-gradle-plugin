package ru.cian.huawei.publish

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.api.BaseVariant
import org.gradle.api.Plugin
import org.gradle.api.Project

class HuaweiPublishPlugin : Plugin<Project> {

    override fun apply(project: Project) {

        check(project.plugins.hasPlugin("com.android.application")) {
            "Plugin must be applied to the main app plugin but was applied to ${project.path}"
        }

        project.extensions.create(
            HuaweiPublishExtension.NAME,
            HuaweiPublishExtension::class.java,
            project
        )

        project.afterEvaluate {
            if (!project.plugins.hasPlugin(AppPlugin::class.java)) {
                project.logger.warn(
                    "The Android Gradle Plugin was not applied. Gradle Play Publisher " +
                        "will not be configured.")
            }
        }

        val androidExtension = project.extensions.getByType(AppExtension::class.java)
        androidExtension.applicationVariants.all { variant ->
            if (!variant.buildType.isDebuggable) {
                createTask(project, variant)
            }
        }
    }

    private fun createTask(project: Project, variant: BaseVariant) {
        val variantName = variant.name.capitalize()
        project.tasks.create("${HuaweiPublishTask.NAME}$variantName", HuaweiPublishTask::class.java, variant)
    }
}