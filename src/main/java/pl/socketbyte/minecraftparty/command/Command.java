package pl.socketbyte.minecraftparty.command;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
    String name();
    String permission() default "none";
    String usage() default "none";
    String[] aliases() default {};
    boolean onlyPlayer() default true;
}
