package dev.tr7zw.config;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.util.ComponentProvider;
import net.minecraft.Util;
import net.minecraft.client.BooleanOption;
import net.minecraft.client.CycleOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ProgressOption;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.components.SliderButton;
import net.minecraft.client.gui.components.TooltipAccessor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

public abstract class CustomConfigScreen extends Screen {

    protected final Screen lastScreen;
    private OptionsList list;

    public CustomConfigScreen(Screen lastScreen, String title) {
        super(new TranslatableComponent(title));
        this.lastScreen = lastScreen;
    }

    @Override
    public void removed() {
        save();
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }

    public OptionsList getOptions() {
        return list;
    }

    protected void init() {
        this.list = new OptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
        this.addWidget(this.list);
        this.createFooter();
        initialize();
    }

    public abstract void initialize();

    public abstract void save();

    public abstract void reset();

    protected void createFooter() {
        this.addButton(
                new Button(this.width / 2 - 100, this.height - 27, 200, 20, CommonComponents.GUI_DONE, new OnPress() {

                    @Override
                    public void onPress(Button button) {
                        CustomConfigScreen.this.onClose();
                    }
                }));
        this.addButton(new Button(this.width / 2 + 110, this.height - 27, 60, 20,
                new TranslatableComponent("controls.reset"), new OnPress() {

                    @Override
                    public void onPress(Button button) {
                        reset();
                        CustomConfigScreen.this.resize(minecraft, width, height); // refresh
                    }
                }));
        this.addButton(new PlainTextButton(5, 5, 400, 20,
                ComponentProvider.literal("Enjoying the mod? Consider supporting the developer!"), new OnPress() {
                    @Override
                    public void onPress(Button button) {
                        Util.getPlatform().openUri("https://tr7zw.dev/donate/");
                    }
                }, minecraft.font));
    }

    public void render(PoseStack poseStack, int i, int j, float f) {
        this.renderBackground(poseStack);
        this.list.render(poseStack, i, j, f);
        drawCenteredString(poseStack, this.font, this.title, this.width / 2, 20, 16777215);
        List<FormattedCharSequence> tooltip = tooltipAt(list, i, j);
        if (tooltip != null) {
            renderTooltip(poseStack, tooltip, i, j);
        }
        super.render(poseStack, i, j, f);
    }

    private void updateText(ProgressOption option) {
        AbstractWidget widget = getOptions().findOption(option);
        if (widget instanceof SliderButton) {
            ((SliderButton) widget).setMessage(option.getMessage(Minecraft.getInstance().options));
        }
    }

    public BooleanOption getBooleanOption(String translationKey, Supplier<Boolean> current, Consumer<Boolean> update) {
        BooleanOption option = new BooleanOption(translationKey, null, (options) -> current.get(),
                (options, b) -> update.accept(b));
        option.setTooltip(createStaticTooltip(translationKey));
        return option;
    }

    public BooleanOption getOnOffOption(String translationKey, Supplier<Boolean> current, Consumer<Boolean> update) {
        return getBooleanOption(translationKey, current, update);
    }

    public ProgressOption getDoubleOption(String translationKey, float min, float max, float steps,
            Supplier<Double> current, Consumer<Double> update) {
        TranslatableComponent comp = new TranslatableComponent(translationKey);
        ProgressOption option = new ProgressOption(translationKey, min, max, steps, (options) -> current.get(),
                (options, val) -> update.accept(val),
                (options, opt) -> comp.append(new TextComponent(": " + round(opt.get(options), 3))));
        option.setTooltip(createStaticTooltip(translationKey));
        return option;
    }

    public ProgressOption getIntOption(String translationKey, float min, float max, Supplier<Integer> current,
            Consumer<Integer> update) {
        TranslatableComponent comp = new TranslatableComponent(translationKey);
        AtomicReference<ProgressOption> option = new AtomicReference<>();
        option.set(
                new ProgressOption(translationKey, min, max, 1, (options) -> (double) current.get(), (options, val) -> {
                    update.accept(val.intValue());
                    updateText(option.get());
                }, (options, opt) -> comp.copy().append(": " + current.get())));
        option.get().setTooltip(createStaticTooltip(translationKey));
        return option.get();
    }

    public <T extends Enum> CycleOption getEnumOption(String translationKey, Class<T> targetEnum, Supplier<T> current,
            Consumer<T> update) {
        CycleOption option = new CycleOption(translationKey,
                (options, integer) -> update
                        .accept(targetEnum.getEnumConstants()[(current.get().ordinal() + integer.intValue())
                                % targetEnum.getEnumConstants().length]),
                (options, cycleOption) -> {
                    cycleOption.setTooltip(createStaticTooltip(translationKey));
                    return new TranslatableComponent(translationKey).append(": ").append(new TranslatableComponent(translationKey + "." + current.get().name()));
                });

        return option;
    }

    public List<FormattedCharSequence> createStaticTooltip(String translationKey) {
        String key = translationKey + ".tooltip";
        Component comp = new TranslatableComponent(key);
        if (key.equals(comp.getString())) {
            return null;
        } else {
            return minecraft.font.split(comp, 170);
        }
    }

    public static List<FormattedCharSequence> tooltipAt(OptionsList arg, int i, int j) {
    	if(!arg.isMouseOver(i, j))return null;
        Optional<AbstractWidget> optional = arg.getMouseOver(i, j);
        if (optional.isPresent() && optional.get() instanceof TooltipAccessor) {
            Optional<List<FormattedCharSequence>> optional2 = ((TooltipAccessor) optional.get()).getTooltip();
            return optional2.orElse(null);
        }
        return null;
    }
    
    public static double round(double value, int places) {
        if (places < 0)
            throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
    
    public class PlainTextButton extends Button {
    	private final Font font;
    	private final Component message;
    	private final Component underlinedMessage;

    	public PlainTextButton(int x, int y, int width, int height, Component message, OnPress onPress, Font font) {
    		super(x, y, width, height, message, onPress);
    		this.font = font;
    		this.message = message;
    		this.underlinedMessage = ComponentUtils.mergeStyles(message.copy(), Style.EMPTY.withUnderlined(true));
    	}

    	public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
    		Component component = this.isHovered() ? this.underlinedMessage : this.message;
    		drawString(poseStack, this.font, component, this.x, this.y,
    				16777215 | Mth.ceil(this.alpha * 255.0F) << 24);
    	}
    }

}