package pub.ihub.agent.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
    name = "ihub",
    description = "IHub 命令行入口 — 项目管理、AI 元数据生成、健康度检查",
    mixinStandardHelpOptions = true,
    subcommands = {
        IHubCli.InitCommand.class,
        IHubCli.MetaCommand.class,
        IHubCli.CheckCommand.class
    }
)
public class IHubCli {

    public static void main(String[] args) {
        int exitCode = new CommandLine(new IHubCli()).execute(args);
        System.exit(exitCode);
    }

    @Command(name = "init", description = "初始化 IHub 项目结构")
    static class InitCommand implements Runnable {
        @Option(names = {"--name"}, description = "项目名称")
        String name;

        @Override
        public void run() {
            System.out.println("P2: 实现项目初始化逻辑");
        }
    }

    @Command(name = "meta", description = "输出项目 AI 元数据（JSON）")
    static class MetaCommand implements Runnable {
        @Override
        public void run() {
            System.out.println("P2: 实现 AI 元数据输出逻辑");
        }
    }

    @Command(name = "check", description = "检查项目健康度与最佳实践")
    static class CheckCommand implements Runnable {
        @Override
        public void run() {
            System.out.println("P2: 实现健康度检查逻辑");
        }
    }
}
