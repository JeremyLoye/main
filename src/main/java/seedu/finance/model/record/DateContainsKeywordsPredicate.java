package seedu.finance.model.record;

import seedu.finance.commons.util.StringUtil;
import seedu.finance.model.category.Category;

import java.util.List;
import java.util.function.Predicate;


/**
 * Tests that a {@code Record}'s {@code Category} matches any of the keywords given.
 */
public class DateContainsKeywordsPredicate implements Predicate<Record> {
    private final List<String> listOfDates;

    public DateContainsKeywordsPredicate(List<String> listOfDates) {
        this.listOfDates = listOfDates;
    }

    @Override
    public boolean test(Record record) {
        return listOfDates.stream()
                .anyMatch(date -> StringUtil.containsWordIgnoreCase(record.getDate().toString(), date));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof DateContainsKeywordsPredicate // instanceof handles nulls
                && listOfDates.equals(((DateContainsKeywordsPredicate) other).listOfDates)); // state check
    }

}
