package omtteam.openmodularturrets.reference;

public class Reference {
    public static final String MOD_ID = "openmodularturrets";
    public static final String NAME = "Open Modular Turrets";
    public static final String VERSION = "@VERSION@";
    public static final String ACCEPTED_MINECRAFT_VERSION = "[1.9,1.12)";
    private static final String OMLIB_MIN_VERSION = "3.0.0-69";
    public static final String DEPENDENCIES = "after:ThermalFoundation;after:ThermalExpansion;after:OpenComputers;" +
            "after:ComputerCraft;after:Mekanism;after:EnderIO;after:Thaumcraft;required-after:omlib@" + OMLIB_MIN_VERSION;
}