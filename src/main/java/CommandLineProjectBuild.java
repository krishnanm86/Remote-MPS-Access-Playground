import jetbrains.mps.ide.ThreadUtils;
import jetbrains.mps.project.Project;
import jetbrains.mps.project.ProjectModelAccess;
import jetbrains.mps.smodel.ModuleRepositoryFacade;
import jetbrains.mps.tool.environment.Environment;
import jetbrains.mps.tool.environment.EnvironmentConfig;
import jetbrains.mps.tool.environment.IdeaEnvironment;
import org.jetbrains.mps.openapi.language.SProperty;
import org.jetbrains.mps.openapi.model.EditableSModel;
import org.jetbrains.mps.openapi.model.SNode;
import java.io.File;

public class CommandLineProjectBuild {

    public static void main(String[] args) {
        final String projectPath = "./UserMpsProject";
        final EnvironmentConfig config = EnvironmentConfig
                .emptyConfig()
                .withDefaultPlugins()
                .withBootstrapLibraries();
        final Environment env = IdeaEnvironment.getOrCreate(config);

        System.out.println("Check if environment is running " + env.getPlatform());
        final Project project = env.openProject(new File(projectPath));
        Throwable thrown = null;
        try {
            System.out.println("#####################################################");
            // TODO: Do something with the project
            System.out.println("project name: "+ project.getName());
            ModuleRepositoryFacade facade = new ModuleRepositoryFacade(project);
            System.out.println("#####################################################");
            ProjectModelAccess m = new ProjectModelAccess(project);
            command1(facade, m);
            command2(facade, m);
            System.out.println("Check if environment is running " + env.getPlatform());
        } catch (Exception e) {
            thrown = e;
        }
        if (thrown != null) {
            System.err.println("ERROR:");
            thrown.printStackTrace();
            System.exit(1);
        } else {
            System.out.println("EnvironmentConfig of MPS successfully loaded");
            // System.exit is needed even in case of success to kill threads that MPS plugins may be leaving behind.
            System.exit(0);
        }
    }

    private static void command1(ModuleRepositoryFacade facade, ProjectModelAccess m) {
        ThreadUtils.runInUIThreadAndWait(new Runnable() {
            @Override
            public void run() {
                m.executeCommand(new Runnable() {
                    @Override
                    public void run() {
                        for(org.jetbrains.mps.openapi.model.SModel mdl : facade.getAllModels()){
                            if(mdl.getName().getLongName().equals("solution1.modelRemoteAccess")){
                                System.out.println("Model "+ mdl.getName());
                                for(SNode node: mdl.getRootNodes()){
                                    System.out.println("Node " + node.getPresentation());
                                    for(SProperty prop: node.getProperties()){
                                        System.out.println("property " + prop.getName() + " = " + node.getProperty(prop));
                                        System.out.println("Changing value of the property");
                                        node.setProperty(prop, "30");
                                        System.out.println("property " + prop.getName() + " = " + node.getProperty(prop));
                                    }
                                }
                                ((EditableSModel)mdl).save();
                            }
                        }
                    }
                });
            }
        });
    }

    private static void command2(ModuleRepositoryFacade facade, ProjectModelAccess m) {
        ThreadUtils.runInUIThreadAndWait(new Runnable() {
            @Override
            public void run() {
                m.executeCommand(new Runnable() {
                    @Override
                    public void run() {
                        for(org.jetbrains.mps.openapi.model.SModel mdl : facade.getAllModels()){
                            if(mdl.getName().getLongName().equals("solution1.modelRemoteAccess")){
                                System.out.println("Model 2"+ mdl.getName());
                                for(SNode node: mdl.getRootNodes()){
                                    System.out.println("Node 2" + node.getPresentation());
                                    for(SProperty prop: node.getProperties()){
                                        System.out.println("property  2" + prop.getName() + " = " + node.getProperty(prop));
                                        System.out.println("Changing value of the property 2");
                                        node.setProperty(prop, "40");
                                        System.out.println("property " + prop.getName() + " = " + node.getProperty(prop));
                                    }
                                }
                                ((EditableSModel)mdl).save();
                            }
                        }
                    }
                });
            }
        });
    }
}
