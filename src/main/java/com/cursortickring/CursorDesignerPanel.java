package com.cursortickring;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.HierarchyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.Timer;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;
import net.runelite.client.ui.components.colorpicker.RuneliteColorPicker;

final class CursorDesignerPanel extends PluginPanel
{
	private final ConfigManager configManager;
	private final ColorPickerManager colorPickerManager;
	private final List<Runnable> updates = new ArrayList<>();
	private final JPanel pages = new JPanel(new CardLayout());
	private final Timer timer;
	private JPanel page;
	private RuneliteColorPicker colorPicker;
	private boolean refreshing;

	CursorDesignerPanel(CursorTickPlugin plugin, CursorTickConfig config,
		ConfigManager configManager, ColorPickerManager colorPickerManager)
	{
		super(false);
		this.configManager = configManager;
		this.colorPickerManager = colorPickerManager;
		setLayout(new BorderLayout(0, 10));
		setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

		Preview preview = new Preview(plugin, config);
		timer = new Timer(16, event -> preview.repaint());
		preview.addHierarchyListener(event ->
		{
			if ((event.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0)
			{
				if (preview.isShowing())
				{
					timer.start();
				}
				else
				{
					timer.stop();
				}
			}
		});
		JComboBox<String> sections = new JComboBox<>(new String[]{"Ring", "Cursor", "Label", "Effects", "Timing"});
		JPanel header = new JPanel(new BorderLayout(0, 8));
		header.add(new JLabel("Cursor designer"), BorderLayout.NORTH);
		header.add(preview, BorderLayout.CENTER);
		header.add(sections, BorderLayout.SOUTH);
		add(header, BorderLayout.NORTH);
		JScrollPane scroll = new JScrollPane(pages);
		scroll.setBorder(BorderFactory.createEmptyBorder());
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.getVerticalScrollBar().setUnitIncrement(16);
		add(scroll, BorderLayout.CENTER);

		section("Ring");
		choice("Style", "ringStyle", RingStyle.values(), config::ringStyle);
		choice("Direction", "rotationDirection", RotationDirection.values(), config::rotationDirection);
		slider("Radius", "radius", 6, 100, config::radius);
		slider("Thickness", "thickness", 1, 20, config::thickness);
		slider("Start angle", "startAngle", 0, 359, config::startAngle);
		choice("Line ends", "lineCap", LineCap.values(), config::lineCap);
		color("Progress", "progressColor", config::progressColor);
		toggle("Background track", "showTrack", config::showTrack);
		color("Track", "trackColor", config::trackColor);
		toggle("Outline", "showOutline", config::showOutline);
		slider("Outline width", "outlineWidth", 1, 5, config::outlineWidth);
		color("Outline", "outlineColor", config::outlineColor);
		slider("Segments", "segments", 1, 24, config::segments);
		slider("Segment gap", "segmentGap", 0, 30, config::segmentGap);
		slider("Sweep size", "sweepSize", 5, 270, config::sweepSize);
		toggle("Leading marker", "showLeadingDot", config::showLeadingDot);
		slider("Leading marker size", "leadingDotSize", 2, 12, config::leadingDotSize);
		toggle("Smooth tick reset", "tickResetFade", config::fadeOnTickReset);
		slider("Reset fade (ms)", "tickResetFadeDuration", 25, 600, config::tickResetFadeDuration);
		toggle("Smooth edges", "antiAliasing", config::antiAliasing);

		section("Cursor");
		choice("Cursor", "cursorMode", CursorMode.values(), config::cursorMode);
		slider("Marker size", "cursorMarkerSize", 3, 40, config::cursorMarkerSize);
		slider("Marker thickness", "cursorMarkerThickness", 1, 8, config::cursorMarkerThickness);
		slider("Crosshair gap", "cursorMarkerGap", 0, 12, config::cursorMarkerGap);
		color("Marker", "cursorMarkerColor", config::cursorMarkerColor);
		toggle("Marker outline", "cursorMarkerOutline", config::cursorMarkerOutline);
		color("Marker outline", "cursorMarkerOutlineColor", config::cursorMarkerOutlineColor);
		slider("Horizontal offset", "horizontalOffset", -100, 100, config::horizontalOffset);
		slider("Vertical offset", "verticalOffset", -100, 100, config::verticalOffset);
		slider("Idle after (seconds)", "idleFadeDelaySeconds", 0, 300, config::idleFadeDelaySeconds);
		slider("Idle fade (seconds)", "idleFadeDurationSeconds", 0, 30, config::idleFadeDurationSeconds);

		section("Label");
		toggle("Attack timer mode", "attackTimerMode", config::attackTimerMode);
		toggle("Show tick label", "showTickLabel", config::showTickLabel);
		choice("Label value", "tickLabelMode", TickLabelMode.values(), config::tickLabelMode);
		slider("Cycle length", "cycleLength", 1, 32, config::cycleLength);
		choice("Position", "tickLabelPosition", TickLabelPosition.values(), config::tickLabelPosition);
		slider("Font size", "labelFontSize", 8, 32, config::labelFontSize);
		toggle("Bold", "boldLabel", config::boldLabel);
		toggle("Text shadow", "labelShadow", config::labelShadow);
		color("Label", "labelColor", config::labelColor);
		toggle("Cycle-start accent", "cycleStartAccent", config::cycleStartAccent);
		color("Cycle start", "cycleStartColor", config::cycleStartColor);
		toggle("Accent last attack tick", "attackLastTickAccent", config::attackLastTickAccent);
		color("Last attack tick", "attackLastTickColor", config::attackLastTickColor);

		section("Effects");
		toggle("Feedback outlines", "showFeedbackOutline", config::showFeedbackOutline);
		toggle("Pulse on tick", "showTickFlash", config::pulseOnTick);
		slider("Pulse cycle tick (0 = all)", "pulseCycleTick", 0, 32, config::pulseCycleTick);
		slider("Tick pulse (ms)", "tickFlashDuration", 25, 500, config::pulseOnTickDuration);
		slider("Tick expansion", "tickFlashExpansion", 0, 30, config::pulseOnTickExpansion);
		color("Tick pulse", "tickFlashColor", config::pulseOnTickColor);
		toggle("Click pulse", "showClickPulse", config::showClickPulse);
		choice("Click anchor", "clickAnchor", ClickAnchor.values(), config::clickAnchor);
		slider("Click pulse (ms)", "clickPulseDuration", 50, 1200, config::clickPulseDuration);
		slider("Click expansion", "clickPulseExpansion", 0, 50, config::clickPulseExpansion);
		slider("Click thickness", "clickPulseThickness", 1, 12, config::clickPulseThickness);
		color("Left click", "leftClickColor", config::leftClickColor);
		color("Right click", "rightClickColor", config::rightClickColor);
		color("Middle click", "middleClickColor", config::middleClickColor);

		section("Timing");
		choice("Timing mode", "timingMode", TimingMode.values(), config::timingMode);
		slider("Tick duration (ms)", "tickDuration", 400, 1000, config::tickDuration);
		slider("Adaptation strength", "adaptationStrength", 1, 100, config::adaptationStrength);
		slider("Phase offset (ms)", "phaseOffset", -250, 250, config::phaseOffset);
		refresh();
		sections.addActionListener(event ->
		{
			((CardLayout) pages.getLayout()).show(pages, (String) sections.getSelectedItem());
			pages.setPreferredSize(new Dimension(0,
				pages.getComponent(sections.getSelectedIndex()).getPreferredSize().height));
			pages.revalidate();
			scroll.getVerticalScrollBar().setValue(0);
		});
		sections.setSelectedIndex(0);
	}

	void refresh()
	{
		refreshing = true;
		try
		{
			updates.forEach(Runnable::run);
		}
		finally
		{
			refreshing = false;
		}
	}

	void dispose()
	{
		timer.stop();
		if (colorPicker != null)
		{
			colorPicker.dispose();
		}
	}

	private void save(String key, Object value)
	{
		if (!refreshing)
		{
			configManager.setConfiguration(CursorTickConfig.GROUP, key, value);
		}
	}

	private void section(String name)
	{
		page = new JPanel();
		page.setLayout(new BoxLayout(page, BoxLayout.Y_AXIS));
		JPanel wrapper = new JPanel(new BorderLayout());
		wrapper.add(page, BorderLayout.NORTH);
		pages.add(wrapper, name);
	}

	private void control(String name, JComponent component)
	{
		JPanel row = new JPanel(new BorderLayout(0, 4));
		row.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 6));
		if (name != null)
		{
			row.add(new JLabel(name), BorderLayout.NORTH);
		}
		row.add(component, BorderLayout.CENTER);
		page.add(row);
	}

	private void slider(String name, String key, int min, int max, Supplier<Integer> value)
	{
		JSlider slider = new JSlider(min, max, value.get());
		slider.setOpaque(false);
		JLabel label = new JLabel();
		JPanel row = new JPanel(new BorderLayout());
		row.add(label, BorderLayout.NORTH);
		row.add(slider, BorderLayout.CENTER);
		slider.addChangeListener(event ->
		{
			label.setText(name + "  " + slider.getValue());
			save(key, slider.getValue());
		});
		updates.add(() ->
		{
			if (!slider.getValueIsAdjusting())
			{
				slider.setValue(value.get());
			}
			label.setText(name + "  " + slider.getValue());
		});
		control(null, row);
	}

	private void toggle(String name, String key, Supplier<Boolean> value)
	{
		JCheckBox checkbox = new JCheckBox(name);
		checkbox.setOpaque(false);
		checkbox.addActionListener(event -> save(key, checkbox.isSelected()));
		updates.add(() -> checkbox.setSelected(value.get()));
		control(null, checkbox);
	}

	private <T> void choice(String name, String key, T[] values, Supplier<T> value)
	{
		JComboBox<T> combo = new JComboBox<>(values);
		combo.addActionListener(event -> save(key, combo.getSelectedItem()));
		updates.add(() -> combo.setSelectedItem(value.get()));
		control(name, combo);
	}

	private void color(String name, String key, Supplier<Color> value)
	{
		JButton button = new JButton();
		button.setOpaque(true);
		button.setFocusPainted(false);
		button.addActionListener(event ->
		{
			if (colorPicker != null)
			{
				colorPicker.dispose();
			}
			colorPicker = colorPickerManager.create(button, value.get(), name, true);
			colorPicker.setOnColorChange(selected -> save(key, selected));
			colorPicker.setVisible(true);
		});
		updates.add(() ->
		{
			Color selected = value.get();
			button.setBackground(new Color(selected.getRGB()));
			button.setForeground(selected.getRed() * 299 + selected.getGreen() * 587
				+ selected.getBlue() * 114 > 150000 ? Color.BLACK : Color.WHITE);
			button.setText(String.format("#%06X   %d%%", selected.getRGB() & 0xFFFFFF,
				Math.round(selected.getAlpha() * 100f / 255)));
		});
		control(name, button);
	}

	static BufferedImage createIcon()
	{
		BufferedImage icon = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = icon.createGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setColor(new Color(0, 255, 170));
		graphics.setStroke(new BasicStroke(2));
		graphics.drawArc(3, 3, 14, 14, 30, 290);
		graphics.fillOval(8, 8, 4, 4);
		graphics.dispose();
		return icon;
	}

	private static final class Preview extends JPanel
	{
		private final CursorTickConfig config;
		private final CursorTickOverlay renderer;
		private final TickClock clock = new TickClock();
		private ClickPulse click;

		private Preview(CursorTickPlugin plugin, CursorTickConfig config)
		{
			this.config = config;
			renderer = new CursorTickOverlay(null, plugin, config);
			setPreferredSize(new Dimension(0, 180));
			setBackground(new Color(28, 29, 32));
			setToolTipText("Live preview, scaled to fit. Click to preview feedback. Changes save automatically.");
			addMouseListener(new MouseAdapter()
			{
				@Override
				public void mousePressed(MouseEvent event)
				{
					click = new ClickPulse(System.nanoTime(), 0, 0, event.getButton());
				}
			});
		}

		@Override
		protected void paintComponent(Graphics graphics)
		{
			super.paintComponent(graphics);
			long now = System.nanoTime();
			if (clock.getState().ageMillisAt(now) >= config.tickDuration())
			{
				clock.onGameTick(now, config.tickDuration(), TimingMode.FIXED, 0);
			}
			int extent = config.radius() + config.thickness() + config.outlineWidth()
				+ Math.max(config.clickPulseExpansion(), config.pulseOnTickExpansion()) + 12;
			double scale = Math.min(1.0, Math.min(
				getWidth() / (2.0 * (extent + Math.abs(config.horizontalOffset()))),
				getHeight() / (2.0 * (extent + Math.abs(config.verticalOffset()) + config.labelFontSize() + 8))));
			Graphics2D g = (Graphics2D) graphics.create();
			try
			{
				g.translate(getWidth() / 2.0, getHeight() / 2.0);
				g.scale(scale, scale);
				renderer.drawPreview(g, 0, 0, clock.getState(), now, click);
			}
			finally
			{
				g.dispose();
			}
		}
	}
}
