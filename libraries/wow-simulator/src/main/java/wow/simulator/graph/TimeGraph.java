package wow.simulator.graph;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import wow.commons.model.Duration;
import wow.simulator.model.time.Time;
import wow.simulator.model.unit.Player;
import wow.simulator.util.SimpleId;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Map;
import java.util.TreeMap;

/**
 * User: POlszewski
 * Date: 2023-08-20
 */
@Slf4j
public class TimeGraph {
	private static final int MARGIN = 20;
	private static final int LANE_DESCR_WIDTH = 150;
	private static final int LANE_HEIGHT = 30;
	private static final int LANE_SPACE = 4;
	private static final int PX_PER_1_SEC = 20;
	private static final int RULER_HEIGHT = 4;
	private static final int MSG_HEIGHT = 16;

	private static final Color OUTLINE_COLOR = new Color(201, 201, 201);
	private static final Font SMALL_FONT = new Font("Arial", Font.PLAIN, 9);
	private static final Font LANE_DESCR_FONT = new Font("Courier New", Font.PLAIN, 12);
	private static final Font SUMMARY_FONT = new Font("Courier New", Font.PLAIN, 12);

	private final Map<Integer, Lane> laneById = new TreeMap<>();
	private final GraphSummary summary = new GraphSummary();

	private Time startTime;
	private Time endTime;

	private Graphics2D g2d;

	public void beginSegment(Lane lane, SimpleId segmentId, Color color, Time time) {
		lane.addSegment(segmentId, time, color);

		laneById.put(lane.getLaneId(), lane);
	}

	public void endSegment(Lane lane, SimpleId segmentId, Time time) {
		lane.endSegment(segmentId, time);
	}

	public void gcdEndSegment(Lane lane, SimpleId segmentId, Time time) {
		lane.gcdEndSegment(segmentId, time);
	}

	public void addResistedMark(Lane lane, SimpleId segmentId) {
		lane.addResistedMark(segmentId);
	}

	public void setStartTime(Time startTime) {
		this.startTime = startTime;
	}

	public void endUnfinishedSegments(Time time) {
		this.endTime = time;
		laneById.values().forEach(lane -> lane.endUnfinishedSegments(time));
	}

	public void addSummary(Player player, Statistics statistics) {
		summary.addSummary(player, statistics);
	}

	@SneakyThrows
	public void saveToFile(File file) {
		int height = getImageHeight();
		int width = getImageWidth();

		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		g2d = bufferedImage.createGraphics();

		g2d.setColor(Color.white);
		g2d.fillRect(0, 0, width, height);

		drawElements();

		g2d.dispose();

		file.mkdirs();
		ImageIO.write(bufferedImage, "png", file);

		log.info("Saved to " + file);
	}

	private void drawElements() {
		int laneXOffset = MARGIN + LANE_DESCR_WIDTH;
		int laneYOffset = MARGIN;

		drawRuler(laneXOffset, MARGIN);

		for (Lane lane : laneById.values()) {
			drawLane(laneXOffset, laneYOffset, lane);
			laneYOffset += LANE_HEIGHT + LANE_SPACE;
		}

		drawSummary(laneXOffset, laneYOffset + 2 * MARGIN);
	}

	private int getImageWidth() {
		return MARGIN + LANE_DESCR_WIDTH + t2Px(getDuration()) + MARGIN;
	}

	private Duration getDuration() {
		return endTime.subtract(startTime);
	}

	private int getImageHeight() {
		int summaryHeight = summary.getMaxSummaryRows() * MSG_HEIGHT;
		int numLanes = laneById.size();
		int graphHeight = numLanes * LANE_HEIGHT + (numLanes - 1) * LANE_SPACE;
		return MARGIN + graphHeight + 2 * MARGIN + summaryHeight + MARGIN;
	}

	private void drawLane(int laneXOffset, int laneYOffset, Lane lane) {
		drawLaneLabel(laneYOffset, lane);
		drawLaneOutline(laneXOffset, laneYOffset);

		for (Segment segment : lane.getSegments()) {
			drawSegment(segment, laneXOffset, laneYOffset);
		}
	}

	private void drawLaneLabel(int laneYOffset, Lane lane) {
		g2d.setColor(darken(lane.getDefaultColor() != null ? lane.getDefaultColor() : Color.black));
		g2d.setFont(LANE_DESCR_FONT);

		String label = lane.getLabel() != null ? lane.getLabel() : lane.getObjectId().toString();
		g2d.drawString(label, MARGIN, laneYOffset + g2d.getFont().getSize() + 6);
	}

	private void drawLaneOutline(int laneXOffset, int laneYOffset) {
		g2d.setColor(OUTLINE_COLOR);
		g2d.drawRect(laneXOffset, laneYOffset, t2Px(getDuration()), LANE_HEIGHT);
	}

	private void drawSegment(Segment segment, int laneXOffset, int laneYOffset) {
		int x = laneXOffset + t2Px(segment.getBegin().subtract(startTime));
		int y = laneYOffset;
		int w = t2Px(segment.getActualDuration());
		int h = LANE_HEIGHT;

		g2d.setColor(segment.getColor());
		g2d.fillRect(x, y, w, h);

		g2d.setColor(darken(segment.getColor()));
		g2d.drawRect(x, y, w, h);

		if (segment.hasGcdMarker()) {
			drawGcdMarker(segment, laneXOffset, laneYOffset);
		}

		if (segment.isResisted()) {
			drawResistedMarker(segment, laneXOffset, laneYOffset);
		}

		g2d.setColor(Color.black);
		g2d.setFont(SMALL_FONT);
		g2d.drawString(prettyFormat(segment), x + 4, y + g2d.getFont().getSize());
	}

	private void drawGcdMarker(Segment segment, int laneXOffset, int laneYOffset) {
		int x = laneXOffset + t2Px(segment.getEnd().subtract(startTime));
		int y = laneYOffset;
		int w = t2Px(segment.getGdcAdjustment());
		int h = LANE_HEIGHT;

		g2d.setColor(darken(segment.getColor()));
		g2d.drawLine(x + w, y, x, y + h);
	}

	private void drawResistedMarker(Segment segment, int laneXOffset, int laneYOffset) {
		int x = laneXOffset + t2Px(segment.getEnd().subtract(startTime));
		int y = laneYOffset;
		int w = t2Px(segment.getActualDuration());
		int h = LANE_HEIGHT;

		g2d.setColor(Color.red);
		g2d.drawLine(x, y, x + w, y + h);
	}

	private void drawRuler(int laneXOffset, int laneYOffset) {
		int start = (int) startTime.secondsSinceZero();
		int end = (int) endTime.secondsSinceZero();

		for (int t = start; t <= end; ++t) {
			drawRulerSegment(laneXOffset, laneYOffset, t - start, "" + t);
		}
	}

	private void drawRulerSegment(int laneXOffset, int laneYOffset, int t, String marking) {
		int x1 = laneXOffset + t2Px(t);
		int y1 = laneYOffset - RULER_HEIGHT;
		int y2 = y1 + RULER_HEIGHT;

		g2d.setColor(OUTLINE_COLOR);
		g2d.drawLine(x1, y1, x1, y2);

		g2d.setFont(SMALL_FONT);
		g2d.drawString(marking, x1 - 2, y1 - 5);
	}

	private void drawSummary(int laneXOffset, int laneYOffset) {
		for (var entry : summary.getMessagesByXOffset().entrySet()) {
			int xOffset = entry.getKey();
			int yOffset = laneYOffset;

			for (String message : entry.getValue()) {
				drawSummaryMessage(laneXOffset + xOffset, yOffset, message);
				yOffset += MSG_HEIGHT;
			}
		}
	}

	private void drawSummaryMessage(int xOffset, int yOffset, String message) {
		g2d.setColor(Color.black);
		g2d.setFont(SUMMARY_FONT);
		g2d.drawString(message, xOffset, yOffset);
	}

	private static int t2Px(double t) {
		return (int)(t * PX_PER_1_SEC);
	}

	private static int t2Px(Duration duration) {
		return t2Px(duration.getSeconds());
	}

	private static Color darken(Color color) {
		float[] hsv = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
		hsv[2] *= 0.6;
		return Color.getHSBColor(hsv[0], hsv[1], hsv[2]);
	}

	private static String prettyFormat(Segment segment) {
		String s;
		long millis = segment.getActualDuration().millis();
		if (millis % 1000 == 0) {
			s = "" + (millis / 1000);
		} else {
			s = "" + (millis / 1000.0);
		}
		return s;
	}
}
