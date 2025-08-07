package locationalArmorAddon;

import bodyhealth.api.addons.AddonDebug;
import bodyhealth.api.addons.AddonFileManager;
import bodyhealth.api.addons.AddonInfo;
import bodyhealth.api.addons.BodyHealthAddon;
import bodyhealth.listeners.UpdateNotifyListener;
import bodyhealth.util.UpdateChecker;
import locationalArmorAddon.config.Config;
import locationalArmorAddon.listeners.BodyHealthListener;

import java.io.File;

@AddonInfo(
    name = "LocationalArmorAddon",
    description = "Adds locational armor to BodyHealth",
    version = "1.0.0",
    author = "Mitality"
)
public final class Main extends BodyHealthAddon {

    private static AddonFileManager fileManager;
    private static AddonDebug debug;
    private static Main instance;

    @Override
    public void onAddonEnable() {

        fileManager = getAddonFileManager();
        debug = getAddonDebug();
        instance = this;

        updateAndLoadConfig();

        registerListener(new BodyHealthListener());

        UpdateChecker updateChecker = new UpdateChecker(
            "LocationalArmorAddon",
            "bodyhealthaddon-locationalarmoraddon",
            Main.getInstance().getAddonInfo().version()
        ).checkNow();
        if (bodyhealth.config.Config.update_check_interval > 0) updateChecker
            .checkEveryXHours(bodyhealth.config.Config.update_check_interval);
        registerListener(new UpdateNotifyListener(updateChecker));
    }

    @Override
    public void onBodyHealthReload() {
        updateAndLoadConfig();
    }

    @Override
    public void onAddonDisable() {
        unregisterListeners();
    }

    private static void updateAndLoadConfig() {
        fileManager.saveResource("config.yml", false);
        File configFile = fileManager.getFile("config.yml");
        fileManager.updateYamlFile("config.yml", configFile);
        Config.load(fileManager.getYamlConfiguration("config.yml"));
    }

    public static Main getInstance() {
        return instance;
    }

    public static AddonFileManager getFileManager() {
        return fileManager;
    }

    public static AddonDebug debug() {
        return debug;
    }

}
