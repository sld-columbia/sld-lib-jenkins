#!/usr/bin/env groovy

/**
 * Send notifications based on build status string
 */
def call(String buildStatus = 'STARTED') {
    // build status of null means successful
    buildStatus =  buildStatus ?: 'SUCCESSFUL'

    // Default values
    def colorCode = 'good'
    def branchName = "${env.BRANCH_NAME}"
    // def commit = sh(returnStdout: true, script: 'git rev-parse HEAD')
    def author = sh(returnStdout: true, script: "git --no-pager show -s --format='%an'").trim()
    // def message = sh(returnStdout: true, script: 'git log -1 --pretty=%B').trim()
    // def summary = "${subject} (${env.BUILD_URL})"
    def details = """<p>${buildStatus}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':</p>
    <p>Check console output at &QUOT;<a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BUILD_NUMBER}]</a>&QUOT;</p>"""

    // Override default values based on build status
    if (buildStatus == 'STARTED') {
	colorCode = 'good'
	postStatus = 'Started'
	
    } else if (buildStatus == 'SUCCESSFUL') {
	colorCode = 'good'
	postStatus = 'Success'

    } else if (buildStatus == 'UNSTABLE') {
	colorCode = 'warning'
	postStatus = 'Unstable'

    } else {
	colorCode = 'danger'
	postStatus = 'Failure'
    }

    def subject = "${env.JOB_NAME} - #${env.BUILD_NUMBER} ${postStatus} (<${env.RUN_DISPLAY_URL}|Open>) (<${env.RUN_CHANGES_DISPLAY_URL}|  Changes>). Branch: ${branchName}"
    
    // Send notifications
    slackSend (color: colorCode, message: subject)

    // emailext (
    //     to: 'davide.giri@columbia.edu',
    //    subject: subject,
    //    body: details,
    //    recipientProviders: [[$class: 'DevelopersRecipientProvider']]
    // )
}
