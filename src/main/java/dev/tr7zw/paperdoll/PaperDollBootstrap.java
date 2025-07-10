//#if FORGE
//$$package dev.tr7zw.paperdoll;
//$$
//$$import net.minecraftforge.api.distmarker.Dist;
//$$import net.minecraftforge.fml.DistExecutor;
//$$import net.minecraftforge.fml.common.Mod;
//$$import dev.tr7zw.transition.loader.ModLoaderUtil;
//$$import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
//$$
//$$@Mod("paperdoll")
//$$public class PaperDollBootstrap {
//$$
//$$	public PaperDollBootstrap(FMLJavaModLoadingContext context) {
//$$        ModLoaderUtil.setModLoadingContext(context);
//$$		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> { 
//$$         new PaperDollShared().init();
//$$        });
//$$	}
//$$    public PaperDollBootstrap() {
//$$        this(FMLJavaModLoadingContext.get());
//$$    }
//$$	
//$$}
//#elseif NEOFORGE
//$$package dev.tr7zw.paperdoll;
//$$
//$$import net.neoforged.api.distmarker.Dist;
//$$import net.neoforged.fml.loading.FMLEnvironment;
//$$import net.neoforged.fml.common.Mod;
//$$import dev.tr7zw.transition.loader.ModLoaderEventUtil;
//$$
//$$@Mod("paperdoll")
//$$public class PaperDollBootstrap {
//$$
//$$    public PaperDollBootstrap() {
//$$            if(FMLEnvironment.dist == Dist.CLIENT) {
//$$                    ModLoaderEventUtil.registerClientSetupListener(() -> new PaperDollShared().init());
//$$            }
//$$    }
//$$    
//$$}
//#endif
