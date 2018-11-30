package pl.socketbyte.minecraftparty;

import net.minecraft.server.v1_8_R3.EntityFallingBlock;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import pl.socketbyte.minecraftparty.basic.Game;
import pl.socketbyte.minecraftparty.basic.arena.misc.BaloonEntity;
import pl.socketbyte.minecraftparty.basic.data.Connector;
import pl.socketbyte.minecraftparty.basic.data.ModelPersistence;
import pl.socketbyte.minecraftparty.basic.func.DoubleJump;
import pl.socketbyte.minecraftparty.command.CommandManager;
import pl.socketbyte.minecraftparty.command.impl.JoinGameCommand;
import pl.socketbyte.minecraftparty.commons.NMSHelper;
import pl.socketbyte.minecraftparty.commons.io.I18nConfig;

import java.util.logging.Logger;

public class MinecraftParty extends JavaPlugin {

    private static CommandManager commandManager;
    private static MinecraftParty instance;

    public static MinecraftParty getInstance() {
        return instance;
    }

    private Connector connector;
    private ModelPersistence persistence;

    @Override
    public void onEnable() {
        instance = this;

        Logger log = getLogger();

        log.info("Loading resources...");
        saveDefaultConfig();

        log.info("Loading i18n...");
        new I18nConfig("i18n.yml");

        this.connector = new Connector(
                "localhost",
                "root",
                "",
                "xdxd",
                3306);
        this.persistence = new ModelPersistence(this.connector);

        commandManager = new CommandManager();
        commandManager.registerCommand(new JoinGameCommand());

        NMSHelper.registerEntity("baloon", 65, EntityFallingBlock.class, BaloonEntity.class);

        log.info("Loading games...");
        Game.getController().loadGameInfos();

        Bukkit.getPluginManager().registerEvents(new DoubleJump(), this);

    }

    @Override
    public void onDisable() {
        Game.getController().dispose();
    }
}
