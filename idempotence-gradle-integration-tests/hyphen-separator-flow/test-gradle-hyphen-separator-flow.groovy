//def baseScript = new GroovyScriptEngine( "$project.basedir/src/it" ).with {
def baseScript = new GroovyScriptEngine( "$project.basedir" ).with {
    loadScriptByName( 'GradleIntegrationTest.groovy' )
}
this.metaClass.mixin baseScript

println "Executing test-gradle-hyphen-separator-flow.groovy..."

def targetPath = getTargetPath(project)
def gradlePath = getGradlePath(project)
def executionPath = gradlePath.resolve('hyphen-separator-flow')

def gradlewName
// Determine OS and appropriate gradlew executable
if (System.getProperty("os.name").toLowerCase().contains("windows")) {
    gradlewName = "gradlew.bat"
} else {
    gradlewName = "gradlew"
}

def gradleExec = gradlePath.resolve(gradlewName).toString()

System.out.println(gradleExec)

runCommand(executionPath, String.format("$gradleExec build"))
