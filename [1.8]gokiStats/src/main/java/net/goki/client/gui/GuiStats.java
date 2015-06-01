package net.goki.client.gui;

import net.goki.GokiStats;
import net.goki.handlers.packet.PacketStatAlter;
import net.goki.lib.DataHelper;
import net.goki.stats.Stat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;

import org.lwjgl.util.vector.Vector2f;

public class GuiStats extends GuiScreen
{
	private EntityPlayer player = null;
	public static final int STATUS_BUTTON_WIDTH = 24;
	public static final int STATUS_BUTTON_HEIGHT = 24;
	public static float SCALE = 1.0F;
	// private static final int HORIZONTAL_SPACING = 8;
	// private static final int VERTICAL_SPACING = 12;
	public static final int IMAGE_ROWS = 10;
	private static final int[] COLUMNS = { 4, 3, 5, 3, 5 };

	private int currentColumn = 0;
	private int currentRow = 0;
	private GuiStatTooltip toolTip = null;
	private FontRenderer fontRenderer;

	public GuiStats(EntityPlayer player)
	{
		this.player = player;
		this.fontRenderer = Minecraft.getMinecraft().fontRendererObj;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float par3)
	{
		int ttx = 0;
		int tty = 0;
		this.toolTip = null;
		drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, par3);
		for (int i = 0; i < this.buttonList.size(); i++)
		{
			if ((this.buttonList.get(i) instanceof GuiStatButton))
			{
				GuiStatButton button = (GuiStatButton) this.buttonList.get(i);
				if (button.isUnderMouse(mouseX, mouseY))
				{
					this.toolTip = new GuiStatTooltip((Stat) Stat.stats.get(i), this.player);
					ttx = button.xPosition + 12;
					tty = button.yPosition - 1;
					break;
				}
			}
		}
		drawCenteredString(	fontRenderer,
							StatCollector.translateToLocal("ui.currentxp.name") + DataHelper.getXPTotal(player.experienceLevel,
																										player.experience) + "xp",
							width / 2,
							this.height - 16,
							0xFFFFFFFF);

		if (this.toolTip != null)
			this.toolTip.draw(ttx, tty, 0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initGui()
	{
		for (int stat = 0; stat < Stat.totalStats; stat++)
		{
			Vector2f pos = getButtonPosition(stat);
			this.buttonList.add(new GuiStatButton(stat, (int) pos.x, (int) pos.y, 24, 24, (Stat) Stat.stats.get(stat), this.player));
			this.currentColumn += 1;
			if (this.currentColumn >= COLUMNS[this.currentRow])
			{
				this.currentRow += 1;
				this.currentColumn = 0;
			}
			if (this.currentRow >= COLUMNS.length)
			{
				this.currentRow = (COLUMNS.length - 1);
			}
		}
	}

	private Vector2f getButtonPosition(int n)
	{
		Vector2f vec = new Vector2f();
		int columns = COLUMNS[this.currentRow];
		int x = n % columns;
		int y = this.currentRow;
		int rows = COLUMNS.length;
		int amount = columns;
		float width = amount * 32 * SCALE;
		float height = rows * 36 * SCALE;
		vec.x = (width / amount * x + (this.width - width + 8.0F) / 2.0F);
		vec.y = (height / rows * y + (this.height - height + 12.0F) / 2.0F);
		return vec;
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		if ((button.id >= 0) && (button.id <= Stat.totalStats))
		{
			if ((button instanceof GuiStatButton))
			{
				GuiStatButton statButton = (GuiStatButton) button;
				if (!GuiScreen.isCtrlKeyDown())
				{
					GokiStats.packetPipeline.sendToServer(new PacketStatAlter(Stat.stats.indexOf(statButton.stat), 1));
				}
				else
					GokiStats.packetPipeline.sendToServer(new PacketStatAlter(Stat.stats.indexOf(statButton.stat), -1));
			}
		}
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}
}