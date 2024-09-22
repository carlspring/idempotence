import java.nio.file.Paths

class GradleIntegrationTest {

    def runCommand(targetPath, strList)
    {
        assert (strList instanceof String || (strList instanceof List && strList.each{ it instanceof String } ))

        def output = new StringBuffer()

        def path = targetPath.toFile()

        println "Execute command[s]: "
        if(strList instanceof List) {
            strList.each{ println "${it} " }
        } else {
            println strList
        }

        def proc = strList.execute(null, path)
        proc.in.eachLine { line ->
            output.append(line).append("\n")
            println line
        }
        proc.out.close()
        proc.waitFor()

        println "\n"

        if (proc.exitValue()) {
            println "gave the following error: "
            println "[ERROR] ${proc.getErrorStream()}"
        }

        assert !proc.exitValue()

        return output.toString()
    }

    def getTargetPath(project)
    {
        def targetDir = project.build.directory
        println "Target directory: $targetDir\n\n"

        def targetPath = Paths.get(targetDir)
        return targetPath;
    }

    def getGradlePath(project)
    {
        def baseDir = project.basedir
        println "Base directory: $baseDir\n\n"

        def gradlePath = Paths.get(baseDir.toString())
        println "Gradle path: $gradlePath\n\n"
        return gradlePath;
    }

}
