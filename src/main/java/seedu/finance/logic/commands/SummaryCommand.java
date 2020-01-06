package seedu.finance.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.finance.commons.util.AppUtil.checkArgument;
import static seedu.finance.logic.parser.CliSyntax.PREFIX_PERIOD;
import static seedu.finance.logic.parser.CliSyntax.PREFIX_PERIOD_AMOUNT;

import java.time.LocalDate;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

import seedu.finance.commons.core.EventsCenter;
import seedu.finance.commons.events.ShowSummaryRequestEvent;
import seedu.finance.commons.events.SwapBrowserPanelEvent;
import seedu.finance.logic.CommandHistory;
import seedu.finance.model.Model;
import seedu.finance.model.record.Record;

/**
 * This class consists of methods and properties related to the handling of the 'summary' command
 */
public class SummaryCommand extends Command {

    /**
     * This is the command word and alias which a user needs to type to activate the statistics command
     */
    public static final String COMMAND_WORD = "summary";
    public static final String COMMAND_ALIAS = "overview";

    /**
     * These are messages displayed to the user depending on whether their input is successful or not
     */
    public static final String MESSAGE_SUCCESS = "Successfully updated the summary panel!";
    public static final String MESSAGE_PERIOD_AMOUNT_ERROR = "PERIOD_AMOUNT needs to be a positive integer";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Swaps to or updates the summary panel. "
            + "Parameters: "
            + PREFIX_PERIOD_AMOUNT + "PERIOD_AMOUNT\n "
            + PREFIX_PERIOD + "PERIOD (either 'm' or 'd')\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_PERIOD_AMOUNT + "7 "
            + PREFIX_PERIOD + "d ";

    public static final String MESSAGE_PARAMETERS_FORMAT = "Command should be in format: \n"
            + COMMAND_WORD + " "
            + PREFIX_PERIOD_AMOUNT + "PERIOD_AMOUNT\n "
            + PREFIX_PERIOD + "PERIOD (either 'm' or 'd')\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_PERIOD_AMOUNT + "7 "
            + PREFIX_PERIOD + "d ";

    /**
     *  Enum for SummaryPeriod. SummaryPeriod can either be MONTH or DAY
     */
    public enum SummaryPeriod {
        MONTH, DAY
    }

    /**
     * These are the parameters which the user can set based on what they want to see
     * in the chart
     */
    private final Logger logger = Logger.getLogger("Summary Logger");
    private int periodAmount;
    private SummaryPeriod period;

    /**
     * Constructs a {@code SummaryCommand} object with the set of default parameters.
     */
    public SummaryCommand() { //Put default as past 7 days first, see afterwards if can change to all expenses...
        this(7, "d");
    }

    /**
     * Constructs a {@code SummaryCommand} object with parameters after checking whether parameters are valid.
     * The method checks whether {@code periodAmount} is a positive integer, {@code period} is either "m" or "d"
     *
     * @param periodAmount an int for period amount
     * @param period a string for period
     */
    public SummaryCommand(int periodAmount, String period) {
        requireNonNull(period);
        checkArgument(isValidPeriod(period), MESSAGE_PARAMETERS_FORMAT);
        checkArgument(isValidPeriodAmount(periodAmount), MESSAGE_PERIOD_AMOUNT_ERROR);
        this.periodAmount = periodAmount;
        if ("d".equals(period)) {
            this.period = SummaryPeriod.DAY;
        } else {
            this.period = SummaryPeriod.MONTH;
        }
    }

    /**
     * Executes the relevant steps to display statistics, which can be broken down into two steps.
     * The first step is to call the {@code Model} API to update properties related to statistics like
     * the mode, period and periodAmount.
     * The second step is to post the events in order to show summary.
     *
     * @param model {@code Model} which the command should operate on.
     * @param history {@code CommandHistory} which the command should operate on.
     * @return {@code CommandResult} with the success message
     */
    @Override
    public CommandResult execute(Model model, CommandHistory history) {
        requireNonNull(model);
        model.updateRecordSummaryPredicate(getSummaryPredicate());
        model.updateSummaryPeriod(period);
        model.updatePeriodAmount(periodAmount);
        logger.log(Level.INFO,
                "Showing statistics with periodAmount of " + periodAmount
                        + " in a period of " + period);

        EventsCenter.getInstance().post(new SwapBrowserPanelEvent(SwapBrowserPanelEvent.PanelType.SUMMARY));
        EventsCenter.getInstance().post(new ShowSummaryRequestEvent());
        return new CommandResult(true, MESSAGE_SUCCESS, false, false);
    }

    /**
     * Returns Predicate used for FilteredList based on the current {@code period} and {@code periodAmount}
     * @return Predicate with correct predicate properties
     */
    private Predicate<Record> getSummaryPredicate() {
        LocalDate date;
        if (period == SummaryPeriod.DAY) {
            date = LocalDate.now().minusDays((long) periodAmount);
        } else {
            date = LocalDate.now().minusMonths((long) periodAmount);
        }
        return e -> e.getDate().isAfter(date);
    }

    private boolean isValidPeriod(String period) {
        return "d".equals(period) || "m".equals(period);
    }

    private boolean isValidPeriodAmount(int periodAmount) {
        return periodAmount > 0;
    }

    /**
     * Checks equality of current {@code SummaryCommand} object to another object based on their {@code periodAmount},
     * {@code period} and {@code mode}.
     *
     * @param other Object to compare
     * @return true if equal
     */
    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof SummaryCommand // instanceof handles nulls
                && periodAmount == (((SummaryCommand) other).periodAmount)
                && period == (((SummaryCommand) other).period));
    }
}
