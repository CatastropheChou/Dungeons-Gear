package com.infamous.dungeons_gear.datagen;

import com.infamous.dungeons_gear.DungeonsGear;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

import static com.infamous.dungeons_gear.registry.ItemRegistry.ARMORS;

public class ModItemModelProvider extends ItemModelProvider {

    public ModItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, DungeonsGear.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        registerArmors();
    }

    private void registerArmors() {
        ARMORS.forEach((resourceLocation, itemRegistryObject) ->
                generated(resourceLocation.getPath(), itemLoc(resourceLocation)));
    }

    private void generated(String path, ResourceLocation texture) {
        getBuilder(path).parent(new ModelFile.UncheckedModelFile(mcLoc("item/generated"))).texture("layer0", texture);
    }

    private static ResourceLocation itemLoc(ResourceLocation resourceLocation){
        return new ResourceLocation(resourceLocation.getNamespace(), ITEM_FOLDER + "/armor/" + resourceLocation.getPath());
    }
}
