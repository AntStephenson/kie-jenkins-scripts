import org.kie.jenkins.jobdsl.Constants

// definition of parameters

def javaToolEnv="KIE_JDK1_8"
def kieMainBranch=Constants.BRANCH
def organization=Constants.GITHUB_ORG_UNIT
def javadk=Constants.JDK_VERSION

// +++++++++++++++++++++++++++++++++++++++++++ create a seed job ++++++++++++++++++++++++++++++++++++++++++++++++++++

// create seed job script

def seedJob='''#!/bin/bash -e
cd job-dsls
./gradlew clean test'''

job("a-seed-job") {

    description("this job creates all needed Jenkins jobs")

    label("kie-rhel7&&kie-mem8g&&!master")

    logRotator {
        numToKeep(5)
    }

    jdk("${javadk}")

    scm {
        git {
            remote {
                github("${organization}/kie-jenkins-scripts")
            }
            branch ("${kieMainBranch}")
        }
    }

    triggers {
        scm('H/15 * * * *')
    }

    wrappers {
        timestamps()
        colorizeOutput()
        toolenv("${javaToolEnv}")
        preBuildCleanup()
    }

    steps {
        shell(seedJob)

        jobDsl {
            targets("job-dsls/jobs/**/pr_jobs.groovy\n" +
                    "job-dsls/jobs/**/downstream_pr_jobs.groovy\n" +
                    "job-dsls/jobs/**/compile_downstream_build.groovy\n" +
                    "job-dsls/jobs/**/prod_projects_downstream_production.groovy \n" +
                    "job-dsls/jobs/**/upstream.groovy\n" +
                    "job-dsls/jobs/**/dailyBuild_pipeline.groovy\n" +
                    "job-dsls/jobs/**/dailyBuild_prod_pipeline.groovy\n" +
                    "job-dsls/jobs/**/deploy_jobs.groovy\n" +
                    "job-dsls/jobs/**/kie_jenkinsScripts_PR.groovy\n" +
                    "job-dsls/jobs/**/kie_docs_pr.groovy\n" +
                    "job-dsls/jobs/**/pr_droolsjbpm_tools.groovy\n" +
                    "job-dsls/jobs/**/prodTag_pipeline.groovy\n" +
                    "job-dsls/jobs/**/new_branches_seed_job.groovy\n" +
                    "job-dsls/jobs/**/springboot_pr_job.groovy\n" +
                    "job-dsls/jobs/**/srcclr_scan_pipeline.groovy\n" +
                    "job-dsls/jobs/**/srcclr_scan_job.groovy\n" +
                    "job-dsls/jobs/**/jenkins_shared_libs.groovy")
            useScriptText(false)
            sandbox(false)
            ignoreExisting(false)
            ignoreMissingFiles(false)
            failOnMissingPlugin(true)
            unstableOnDeprecation(true)
            removedJobAction('IGNORE')
            removedViewAction('DELETE')
            //removedConfigFilesAction('IGNORE')
            lookupStrategy('SEED_JOB')
            additionalClasspath("job-dsls/src/main/groovy")
        }
    }
}