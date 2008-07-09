// GoGuiUtil.java

package net.sf.gogui.gogui;

import java.awt.Frame;
import java.awt.Point;
import static java.text.MessageFormat.format;
import net.sf.gogui.game.ConstGame;
import net.sf.gogui.game.ConstNode;
import net.sf.gogui.game.NodeUtil;
import net.sf.gogui.go.GoPoint;
import net.sf.gogui.go.Move;
import net.sf.gogui.go.PointList;
import static net.sf.gogui.gogui.I18n.i18n;
import net.sf.gogui.gtp.AnalyzeType;
import net.sf.gogui.gtp.GtpUtil;
import net.sf.gogui.gui.GuiBoard;
import net.sf.gogui.gui.GuiBoardUtil;
import net.sf.gogui.gui.StatusBar;
import net.sf.gogui.gui.TextViewer;

/** Utility functions for class GoGui. */
public final class GoGuiUtil
{
    public static void showAnalyzeTextOutput(Frame owner, GuiBoard guiBoard,
                                             AnalyzeType type,
                                             GoPoint pointArg, String title,
                                             String response)
    {
        boolean highlight = (type == AnalyzeType.HSTRING
                             || type == AnalyzeType.HPSTRING);
        TextViewer.Listener listener = null;
        if (type == AnalyzeType.PSTRING || type == AnalyzeType.HPSTRING)
            listener = new PointSelectionMarker(guiBoard);
        // Remove first line, if empty (formatted responses frequently start
        // with an empty line to avoid text on the line with the status
        // character)
        response = response.replaceAll("\\A *\n", "");
        TextViewer textViewer = new TextViewer(owner, title, response,
                                               highlight, listener);
        if (pointArg == null)
            textViewer.setLocationByPlatform(true);
        else
        {
            Point location = guiBoard.getLocationOnScreen(pointArg);
            textViewer.setLocation(location);
        }
        textViewer.setVisible(true);
    }

    public static void updateMoveText(StatusBar statusBar, ConstGame game)
    {
        statusBar.setToPlay(game.getToMove());
        ConstNode node = game.getCurrentNode();
        int moveNumber = NodeUtil.getMoveNumber(node);
        int movesLeft = NodeUtil.getMovesLeft(node);
        int totalMoves = moveNumber + movesLeft;
        Move move = node.getMove();
        String variation = NodeUtil.getVariationString(node);
        boolean mainVar = "".equals(variation);
        StringBuilder moveText = new StringBuilder(128);
        if (moveNumber > 0)
        {
            moveText.append(moveNumber);
            moveText.append(' ');
        }
        if (movesLeft > 0)
        {
            moveText.append('(');
            moveText.append(totalMoves);
            moveText.append(") ");
        }

        if (move != null)
        {
            moveText.append(move);
            moveText.append(' ');
        }
        if (! mainVar)
        {
            moveText.append('[');
            moveText.append(variation);
            moveText.append(']');
        }
        String tip = null;
        boolean lastMove = (move != null);
        boolean noLastMove1 = (move == null && moveNumber == 1);
        boolean noLastMoveN = (move == null && moveNumber > 1);
        boolean noMovesLeft = (movesLeft == 0);
        boolean movesLeft1 = (movesLeft > 0 && totalMoves == 1);
        boolean movesLeftN = (movesLeft > 0 && totalMoves > 1);
        if (noLastMove1 && noMovesLeft && mainVar)
            tip = i18n("TT_MOVETEXT_1");
        else if (noLastMoveN && noMovesLeft && mainVar)
            tip = format(i18n("TT_MOVETEXT_2"), moveNumber);
        else if (lastMove && noMovesLeft && mainVar)
            tip = format(i18n("TT_MOVETEXT_3"), moveNumber, move);
        else if (noLastMove1 && movesLeft1 && mainVar)
            tip = i18n("TT_MOVETEXT_4");
        else if (noLastMoveN && movesLeft1 && mainVar)
            tip = format(i18n("TT_MOVETEXT_5"), moveNumber);
        else if (lastMove && movesLeft1 && mainVar)
            tip = format(i18n("TT_MOVETEXT_6"), moveNumber, move);
        else if (noLastMove1 && movesLeftN && mainVar)
            tip = format(i18n("TT_MOVETEXT_7"), totalMoves);
        else if (noLastMoveN && movesLeftN && mainVar)
            tip = format(i18n("TT_MOVETEXT_8"), moveNumber, totalMoves);
        else if (lastMove && movesLeftN && mainVar)
            tip = format(i18n("TT_MOVETEXT_9"), moveNumber, move, totalMoves);
        else if (noLastMove1 && noMovesLeft && ! mainVar)
            tip = format(i18n("TT_MOVETEXT_10"), variation);
        else if (noLastMoveN && noMovesLeft && ! mainVar)
            tip = format(i18n("TT_MOVETEXT_11"), moveNumber, variation);
        else if (lastMove && noMovesLeft && ! mainVar)
            tip = format(i18n("TT_MOVETEXT_12"), moveNumber, move, variation);
        else if (noLastMove1 && movesLeft1 && ! mainVar)
            tip = format(i18n("TT_MOVETEXT_13"), variation);
        else if (noLastMoveN && movesLeft1 && ! mainVar)
            tip = format(i18n("TT_MOVETEXT_14"), moveNumber, variation);
        else if (lastMove && movesLeft1 && ! mainVar)
            tip = format(i18n("TT_MOVETEXT_15"), moveNumber, move, variation);
        else if (noLastMove1 && movesLeftN && ! mainVar)
            tip = format(i18n("TT_MOVETEXT_16"), totalMoves, variation);
        else if (noLastMoveN && movesLeftN && ! mainVar)
            tip = format(i18n("TT_MOVETEXT_17"), moveNumber, totalMoves,
                         variation);
        else if (lastMove && movesLeftN && ! mainVar)
            tip = format(i18n("TT_MOVETEXT_18"), moveNumber, move, totalMoves,
                         variation);
        statusBar.setMoveText(moveText.toString(), tip);
    }

    private static class PointSelectionMarker
        implements TextViewer.Listener
    {
        public PointSelectionMarker(GuiBoard guiBoard)
        {
            m_guiBoard = guiBoard;
        }

        public void textSelected(String text)
        {
            if (! m_guiBoard.isShowing())
                return;
            PointList points = GtpUtil.parsePointString(text);
            GuiBoardUtil.showPointList(m_guiBoard, points);
        }

        private final GuiBoard m_guiBoard;
    }

    /** Make constructor unavailable; class is for namespace only. */
    private GoGuiUtil()
    {
    }
}
