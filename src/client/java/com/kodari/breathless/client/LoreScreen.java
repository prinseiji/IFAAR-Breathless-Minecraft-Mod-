package com.kodari.breathless.client;

import java.util.ArrayList;
import java.util.List;

import com.kodari.breathless.Breathless;
import com.kodari.breathless.Lore;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;

/**
 * Reads a lore document in the open folder (textures/gui/lore_folder.png, 256x256). Two pages per view;
 * the text comes from the language file: breathless.lore.<id>.title / .date / .body (paragraphs separated by a new line).
 * In the text, <r>...</r> is red (glitches) and <k>...</k> is bold (redactions).
 */
public class LoreScreen extends Screen {
   private static final Identifier TEXTURE = Breathless.id("textures/gui/lore_folder.png");
   private static final int TEX = 256;
   /** Where the two pages of text sit inside the picture, and how big the text is drawn. */
   private static final int LEFT_X = 27;
   private static final int RIGHT_X = 141;
   private static final int TEXT_Y = 44;
   private static final int PAGE_W = 90;
   private static final int LINE_STEP = 7;
   private static final int LINES_PER_PAGE = 14;
   private static final float TEXT_SCALE = 0.75F;
   private static final int INK = 0xFF3B2A18;

   private final int doc;
   private final List<List<FormattedCharSequence>> pages = new ArrayList<>();
   private int spread;
   private Button prev;
   private Button next;

   public LoreScreen(int doc) {
      super(Component.translatable("breathless.lore." + Lore.DOCS[doc].id() + ".title"));
      this.doc = doc;
   }

   private int left() {
      return (this.width - TEX) / 2;
   }

   private int top() {
      return (this.height - TEX) / 2 - 8;
   }

   private static MutableComponent parse(String text) {
      MutableComponent out = Component.empty();
      int i = 0;
      while (i < text.length()) {
         int r = text.indexOf("<r>", i);
         int k = text.indexOf("<k>", i);
         int open = r < 0 ? k : (k < 0 ? r : Math.min(r, k));
         if (open < 0) {
            out.append(Component.literal(text.substring(i)));
            break;
         }
         out.append(Component.literal(text.substring(i, open)));
         boolean red = open == r;
         String close = red ? "</r>" : "</k>";
         int end = text.indexOf(close, open);
         if (end < 0) {
            out.append(Component.literal(text.substring(open + 3)));
            break;
         }
         String inner = text.substring(open + 3, end);
         out.append(Component.literal(inner).withStyle(red ? ChatFormatting.DARK_RED : ChatFormatting.BOLD));
         i = end + close.length();
      }
      return out;
   }

   private void layout() {
      Font font = this.font;
      int wrap = Math.round(PAGE_W / TEXT_SCALE);
      List<FormattedCharSequence> lines = new ArrayList<>();
      String id = Lore.DOCS[this.doc].id();
      lines.addAll(font.split(Component.translatable("breathless.lore." + id + ".title").withStyle(ChatFormatting.BOLD, ChatFormatting.DARK_RED), wrap));
      String date = I18n.get("breathless.lore." + id + ".date");
      if (!date.isEmpty()) {
         lines.addAll(font.split(Component.literal(date).withStyle(ChatFormatting.ITALIC), wrap));
      }
      lines.add(FormattedCharSequence.EMPTY);
      for (String paragraph : I18n.get("breathless.lore." + id + ".body").split("\n")) {
         lines.addAll(font.split(parse(paragraph), wrap));
         lines.add(FormattedCharSequence.EMPTY);
      }
      List<FormattedCharSequence> page = new ArrayList<>();
      for (FormattedCharSequence line : lines) {
         if (page.isEmpty() && line == FormattedCharSequence.EMPTY) {
            continue; // no blank line at the top of a page
         }
         page.add(line);
         if (page.size() >= LINES_PER_PAGE) {
            this.pages.add(page);
            page = new ArrayList<>();
         }
      }
      if (!page.isEmpty()) {
         this.pages.add(page);
      }
   }

   private int spreads() {
      return Math.max(1, (this.pages.size() + 1) / 2);
   }

   @Override
   protected void init() {
      this.pages.clear();
      this.layout();
      int cx = this.width / 2;
      int by = this.top() + 218;
      this.prev = this.addRenderableWidget(Button.builder(Component.literal("<"), b -> this.turn(-1)).bounds(cx - 78, by, 24, 20).build());
      this.addRenderableWidget(Button.builder(Component.literal("[ Done ]"), b -> this.onClose()).bounds(cx - 42, by, 84, 20).build());
      this.next = this.addRenderableWidget(Button.builder(Component.literal(">"), b -> this.turn(1)).bounds(cx + 54, by, 24, 20).build());
      this.updateButtons();
   }

   private void turn(int by) {
      this.spread = Math.max(0, Math.min(this.spreads() - 1, this.spread + by));
      this.updateButtons();
   }

   private void updateButtons() {
      this.prev.active = this.spread > 0;
      this.next.active = this.spread < this.spreads() - 1;
   }

   @Override
   public boolean keyPressed(KeyEvent event) {
      if (event.key() == 263) {
         this.turn(-1);
         return true;
      }
      if (event.key() == 262) {
         this.turn(1);
         return true;
      }
      return super.keyPressed(event);
   }

   @Override
   public boolean isPauseScreen() {
      return false;
   }

   @Override
   public void extractBackground(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
      super.extractBackground(g, mouseX, mouseY, partialTick);
      g.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, this.left(), this.top(), 0.0F, 0.0F, TEX, TEX, TEX, TEX);
      this.drawPage(g, this.spread * 2, this.left() + LEFT_X);
      this.drawPage(g, this.spread * 2 + 1, this.left() + RIGHT_X);
   }

   private void drawPage(GuiGraphicsExtractor g, int index, int x) {
      if (index >= this.pages.size()) {
         return;
      }
      List<FormattedCharSequence> page = this.pages.get(index);
      for (int i = 0; i < page.size(); i++) {
         g.pose().pushMatrix();
         g.pose().translate((float)x, (float)(this.top() + TEXT_Y + i * LINE_STEP));
         g.pose().scale(TEXT_SCALE, TEXT_SCALE);
         g.text(this.font, page.get(i), 0, 0, INK, false);
         g.pose().popMatrix();
      }
   }
}
