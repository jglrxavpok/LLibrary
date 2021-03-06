package net.ilexiconn.llibrary.client.gui;

import com.google.common.collect.Lists;
import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.client.gui.element.Element;
import net.ilexiconn.llibrary.client.gui.element.IElementGUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.init.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Basic GUI that supports elements. You don't have to extend this class to use the element API, it just makes it easier.
 *
 * @author iLexiconn
 * @since 1.4.0
 */
@OnlyIn(Dist.CLIENT)
public abstract class ElementGUI extends GuiScreen implements IElementGUI {
    private final Object elementLock = new Object();

    private final List<Element> elements = new ArrayList<>();
    private Element currentlyClicking;
    private long lastClick = System.currentTimeMillis();

    protected abstract void initElements();

    @Override
    public void addElement(Element element) {
        synchronized (this.elementLock) {
            this.elements.add(element);
        }
        element.init();
    }

    @Override
    public void addElements(Collection<Element> elements) {
        elements.forEach(this::addElement);
    }

    @Override
    public void removeElement(Element element) {
        synchronized (this.elementLock) {
            this.elements.remove(element);
            element.dispose();
        }
    }

    @Override
    public void clearElements() {
        synchronized (this.elementLock) {
            this.elements.forEach(Element::dispose);
            this.elements.clear();
        }
    }

    @Override
    public void sendElementToFront(Element element) {
        if (this.elements.contains(element)) {
            synchronized (this.elementLock) {
                this.elements.remove(element);
                this.elements.add(element);
            }
        }
    }

    @Override
    public void sendElementToBack(Element element) {
        if (this.elements.contains(element)) {
            synchronized (this.elementLock) {
                this.elements.remove(element);
                this.elements.add(0, element);
            }
        }
    }

    @Override
    public boolean isElementOnTop(Element element) {
        float mouseX = this.getPreciseMouseX();
        float mouseY = this.getPreciseMouseY();
        synchronized (this.elementLock) {
            List<Element> elements = this.getPostOrderElements();
            for (Element e : elements) {
                if (e.isVisible() && mouseX >= e.getPosX() && mouseY >= e.getPosY() && mouseX < e.getPosX() + e.getWidth() && mouseY < e.getPosY() + e.getHeight()) {
                    return element == e || (element.getParent() != null && element.getParent() == e);
                }
            }
        }
        return false;
    }

    @Override
    public void playClickSound() {
        this.mc.getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0f));
    }

    @Override
    public FontRenderer getFontRenderer() {
        return this.mc.fontRenderer;
    }

    @Override
    public TextureManager getTextureManager() {
        return this.mc.getTextureManager();
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public void initGui() {
        this.initElements();
    }

    @Override
    public void tick() {
        this.getPostOrderElements().forEach(Element::update);
    }

    public abstract void drawScreen(float mouseX, float mouseY, float partialTicks);

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        Gui.drawRect(0, 0, this.width, this.height, LLibrary.CONFIG.getTertiaryColor());

        float preciseMouseX = this.getPreciseMouseX();
        float preciseMouseY = this.getPreciseMouseY();
        this.drawScreen(preciseMouseX, preciseMouseY, partialTicks);

        synchronized (this.elementLock) {
            this.elements.forEach(element -> this.renderElement(element, preciseMouseX, preciseMouseY, partialTicks));
        }
    }

    @Override
    public boolean mouseScrolled(double scrollAmount) {
        for (Element element : this.getPostOrderElements()) {
            if (element.isVisible() && element.isEnabled()) {
                if (element.mouseScrolled(this.getPreciseMouseX(), this.getPreciseMouseY(), (int) Math.ceil(scrollAmount))) {
                    return true;
                }
            }
        }
        return false;
    }

    protected void renderElement(Element element, float mouseX, float mouseY, float partialTicks) {
        if (element.isVisible()) {
            element.renderChildren(mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        lastClick = System.currentTimeMillis();
        float preciseMouseX = this.getPreciseMouseX();
        float preciseMouseY = this.getPreciseMouseY();
        boolean consumed = false;
        synchronized (this.elementLock) {
            List<Element> elements = this.getPostOrderElements();
            for (Element element : elements) {
                if (element.isVisible() && element.isEnabled()) {
                    if (element.mouseClicked(preciseMouseX, preciseMouseY, mouseButton)) {
                        this.currentlyClicking = element;
                        consumed = true;
                        break;
                    }
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton) || consumed;
    }

    @Override
    public boolean mouseDragged(double x, double y, int clickedMouseButton, double dx, double dy) {
        boolean consumed = false;
        float preciseMouseX = this.getPreciseMouseX();
        float preciseMouseY = this.getPreciseMouseY();
        synchronized (this.elementLock) {
            List<Element> elements = this.getPostOrderElements();
            for (Element element : elements) {
                if (element.isVisible() && element.isEnabled() && this.currentlyClicking == element) {
                    if (element.mouseDragged(preciseMouseX, preciseMouseY, clickedMouseButton, timeSinceLastClick())) {
                        consumed = true;
                        break;
                    }
                }
            }
        }
        return super.mouseDragged(x, y, clickedMouseButton, dx, dy) || consumed;
    }

    private long timeSinceLastClick() {
        return System.currentTimeMillis()-lastClick;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int state) {
        float preciseMouseX = this.getPreciseMouseX();
        float preciseMouseY = this.getPreciseMouseY();
        boolean consumed = false;
        synchronized (this.elementLock) {
            List<Element> elements = this.getPostOrderElements();
            for (Element element : elements) {
                if (element.isVisible() && element.isEnabled() && this.currentlyClicking == element) {
                    if (element.mouseReleased(preciseMouseX, preciseMouseY, state)) {
                        consumed = true;
                        break;
                    }
                }
            }
        }
        return super.mouseReleased(mouseX, mouseY, state) || consumed;
    }

    @Override
    public boolean charTyped(char typedChar, int keyCode) {
        boolean consumed = false;
        synchronized (this.elementLock) {
            List<Element> elements = this.getPostOrderElements();
            for (Element element : elements) {
                if (element.isVisible() && element.isEnabled()) {
                    if (element.keyPressed(typedChar, keyCode)) {
                        consumed = true;
                        break;
                    }
                }
            }
        }
        return super.charTyped(typedChar, keyCode) || consumed;
    }

    @Override
    public void onGuiClosed() {
        this.clearElements();
    }

    public float getPreciseMouseX() {
        return (float) Minecraft.getInstance().mouseHelper.getMouseX();
    }

    public float getPreciseMouseY() {
        return (float) Minecraft.getInstance().mouseHelper.getMouseY();
    }

    public Object getElementLock() {
        return this.elementLock;
    }

    public List<Element> getPostOrderElements() {
        List<Element> result = new ArrayList<>();
        this.traverseRecursively(this.elements, result);
        return Lists.reverse(result);
    }

    public List<Element> getPreOrderElements() {
        List<Element> result = new ArrayList<>();
        this.traverseRecursively(this.elements, result);
        return result;
    }

    private void traverseRecursively(List<Element> in, List<Element> out) {
        for (Element element : in) {
            out.add(element);
            this.traverseRecursively(element.getChildren(), out);
        }
    }
}
