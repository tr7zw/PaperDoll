//#if FORGE
//$$package dev.tr7zw.paperdoll;
//$$
//$$import net.minecraftforge.api.distmarker.Dist;
//$$import net.minecraftforge.fml.DistExecutor;
//$$import net.minecraftforge.fml.common.Mod;
//$$
//$$@Mod("paperdoll")
//$$public class PaperDollBootstrap {
//$$
//$$	public PaperDollBootstrap() {
//$$		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> { 
//$$         new PaperDollShared().init();
//$$        });
//$$	}
//$$	
//$$}
//#elseif NEOFORGE
//$$package dev.tr7zw.paperdoll;
//$$
//$$import net.neoforged.api.distmarker.Dist;
//$$import net.neoforged.fml.loading.FMLEnvironment;
//$$import net.neoforged.fml.common.Mod;
//$$
//$$@Mod("paperdoll")
//$$public class PaperDollBootstrap {
//$$
//$$    public PaperDollBootstrap() {
//$$            if(FMLEnvironment.dist == Dist.CLIENT) {
//$$                new PaperDollShared().init();
//$$            }
//$$    }
//$$    
//$$}
//#endif
