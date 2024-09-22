//def baseScript = new GroovyScriptEngine( "$project.basedir/src/it" ).with {
def baseScript = new GroovyScriptEngine( "$project.basedir" ).with {
    loadScriptByName( 'GradleIntegrationTest.groovy' )
}
this.metaClass.mixin baseScript

println "Executing test-gradle-common-flow.groovy..."

def targetPath = getTargetPath(project)
def gradlePath = getGradlePath(project)
//def executionPath = gradlePath.resolve('src').resolve('it').resolve('common-flow')
def executionPath = gradlePath.resolve('common-flow')

def gradlewName
// Determine OS and appropriate gradlew executable
if (System.getProperty("os.name").toLowerCase().contains("windows")) {
//    gradlewName = "src/it/gradlew.bat"
    gradlewName = "gradlew.bat"
} else {
//    gradlewName = "src/it/gradlew"
    gradlewName = "gradlew"
}

def gradleExec = gradlePath.resolve(gradlewName).toString()

System.out.println(gradleExec)

runCommand(executionPath, String.format("$gradleExec build"))
