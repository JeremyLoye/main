package systemtests;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.CommandTestUtil.AMOUNT_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.AMOUNT_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.CATEGORY_DESC_FRIEND;
import static seedu.address.logic.commands.CommandTestUtil.CATEGORY_DESC_HUSBAND;
import static seedu.address.logic.commands.CommandTestUtil.DATE_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.DATE_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_AMOUNT_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_CATEGORY_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_DATE_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_NAME_DESC;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_AMOUNT_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_DATE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CATEGORY;
import static seedu.address.testutil.TypicalRecords.ALICE;
import static seedu.address.testutil.TypicalRecords.AMY;
import static seedu.address.testutil.TypicalRecords.BOB;
import static seedu.address.testutil.TypicalRecords.CARL;
import static seedu.address.testutil.TypicalRecords.HOON;
import static seedu.address.testutil.TypicalRecords.IDA;
import static seedu.address.testutil.TypicalRecords.KEYWORD_MATCHING_MEIER;

import org.junit.Test;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.RedoCommand;
import seedu.address.logic.commands.SpendCommand;
import seedu.address.logic.commands.UndoCommand;
import seedu.address.model.Model;
import seedu.address.model.category.Category;
import seedu.address.model.record.Amount;
import seedu.address.model.record.Date;
import seedu.address.model.record.Name;
import seedu.address.model.record.Record;
import seedu.address.testutil.RecordBuilder;
import seedu.address.testutil.RecordUtil;

public class SpendCommandSystemTest extends AddressBookSystemTest {

    @Test
    public void spend() {
        Model model = getModel();

        /* ------------------------ Perform add operations on the shown unfiltered list ----------------------------- */

        /* Case: add a record without tags to a non-empty address book, command with leading spaces and trailing spaces
         * -> added
         */
        Record toSpend = AMY;
        String command = "   " + SpendCommand.COMMAND_WORD + "  " + NAME_DESC_AMY + "   " + AMOUNT_DESC_AMY + "   "
                + DATE_DESC_AMY + "   " + CATEGORY_DESC_FRIEND + " ";
        assertCommandSuccess(command, toSpend);

        /* Case: undo adding Amy to the list -> Amy deleted */
        command = UndoCommand.COMMAND_WORD;
        String expectedResultMessage = UndoCommand.MESSAGE_SUCCESS;
        assertCommandSuccess(command, model, expectedResultMessage);

        /* Case: redo adding Amy to the list -> Amy added again */
        command = RedoCommand.COMMAND_WORD;
        model.addRecord(toSpend);
        expectedResultMessage = RedoCommand.MESSAGE_SUCCESS;
        assertCommandSuccess(command, model, expectedResultMessage);

        /* Case: add a record with all fields same as another record in the address book except name -> added */
        toSpend = new RecordBuilder(AMY).withName(VALID_NAME_BOB).build();
        command = SpendCommand.COMMAND_WORD + NAME_DESC_BOB + AMOUNT_DESC_AMY + DATE_DESC_AMY + CATEGORY_DESC_FRIEND;
        assertCommandSuccess(command, toSpend);

        /* Case: add a record with all fields same as another record in the address book except amount and date
         * -> added
         */
        toSpend = new RecordBuilder(AMY).withAmount("999").withDate("01/01/2001").build();
        command = RecordUtil.getAddCommand(toSpend);
        assertCommandSuccess(command, toSpend);

        /* Case: add to empty address book -> added */
        deleteAllRecords();
        assertCommandSuccess(ALICE);

        /* Case: add a record with tags, command with parameters in random order -> added */
        toSpend = BOB;
        command = SpendCommand.COMMAND_WORD + CATEGORY_DESC_FRIEND + NAME_DESC_BOB + CATEGORY_DESC_HUSBAND
                + DATE_DESC_BOB + AMOUNT_DESC_BOB;
        assertCommandSuccess(command, toSpend);

        /* Case: add a record, missing tags -> added */
        assertCommandSuccess(HOON);

        /* -------------------------- Perform add operation on the shown filtered list ------------------------------ */

        /* Case: filters the record list before adding -> added */
        showRecordsWithName(KEYWORD_MATCHING_MEIER);
        assertCommandSuccess(IDA);

        /* ------------------------ Perform add operation while a record card is selected --------------------------- */

        /* Case: selects first card in the record list, add a record -> added, card selection remains unchanged */
        selectRecord(Index.fromOneBased(1));
        assertCommandSuccess(CARL);

        /* ----------------------------------- Perform invalid add operations --------------------------------------- */

        /* Case: add a duplicate record -> rejected */
        command = RecordUtil.getAddCommand(HOON);
        assertCommandFailure(command, SpendCommand.MESSAGE_DUPLICATE_RECORD);

        /* Case: add a duplicate record except with different amount -> rejected */
        toSpend = new RecordBuilder(HOON).withAmount(VALID_AMOUNT_BOB).build();
        command = RecordUtil.getAddCommand(toSpend);
        assertCommandFailure(command, SpendCommand.MESSAGE_DUPLICATE_RECORD);

        /* Case: add a duplicate person except with different date -> rejected */
        toSpend = new RecordBuilder(HOON).withDate(VALID_DATE_BOB).build();
        command = RecordUtil.getAddCommand(toSpend);
        assertCommandFailure(command, SpendCommand.MESSAGE_DUPLICATE_RECORD);

        /* Case: add a duplicate record except with different tags -> rejected */
        command = RecordUtil.getAddCommand(HOON) + " " + PREFIX_CATEGORY.getPrefix() + "friends";
        assertCommandFailure(command, SpendCommand.MESSAGE_DUPLICATE_RECORD);

        /* Case: missing name -> rejected */
        command = SpendCommand.COMMAND_WORD + AMOUNT_DESC_AMY + DATE_DESC_AMY;
        assertCommandFailure(command, String.format(MESSAGE_INVALID_COMMAND_FORMAT, SpendCommand.MESSAGE_USAGE));

        /* Case: missing amount -> rejected */
        command = SpendCommand.COMMAND_WORD + NAME_DESC_AMY + DATE_DESC_AMY;
        assertCommandFailure(command, String.format(MESSAGE_INVALID_COMMAND_FORMAT, SpendCommand.MESSAGE_USAGE));

        /* Case: missing date -> rejected */
        command = SpendCommand.COMMAND_WORD + NAME_DESC_AMY + AMOUNT_DESC_AMY;
        assertCommandFailure(command, String.format(MESSAGE_INVALID_COMMAND_FORMAT, SpendCommand.MESSAGE_USAGE));

        /* Case: invalid keyword -> rejected */
        command = "adds " + RecordUtil.getRecordDetails(toSpend);
        assertCommandFailure(command, Messages.MESSAGE_UNKNOWN_COMMAND);

        /* Case: invalid name -> rejected */
        command = SpendCommand.COMMAND_WORD + INVALID_NAME_DESC + AMOUNT_DESC_AMY + DATE_DESC_AMY;
        assertCommandFailure(command, Name.MESSAGE_CONSTRAINTS);

        /* Case: invalid amount -> rejected */
        command = SpendCommand.COMMAND_WORD + NAME_DESC_AMY + INVALID_AMOUNT_DESC + DATE_DESC_AMY;
        assertCommandFailure(command, Amount.MESSAGE_CONSTRAINTS);

        /* Case: invalid date -> rejected */
        command = SpendCommand.COMMAND_WORD + NAME_DESC_AMY + AMOUNT_DESC_AMY + INVALID_DATE_DESC;
        assertCommandFailure(command, Date.MESSAGE_CONSTRAINTS);

        /* Case: invalid category -> rejected */
        command = SpendCommand.COMMAND_WORD + NAME_DESC_AMY + AMOUNT_DESC_AMY + DATE_DESC_AMY + INVALID_CATEGORY_DESC;
        assertCommandFailure(command, Category.MESSAGE_CONSTRAINTS);
    }

    /**
     * Executes the {@code AddCommand} that adds {@code toAdd} to the model and asserts that the,<br>
     * 1. Command box displays an empty string.<br>
     * 2. Command box has the default style class.<br>
     * 3. Result display box displays the success message of executing {@code AddCommand} with the details of
     * {@code toAdd}.<br>
     * 4. {@code Storage} and {@code PersonListPanel} equal to the corresponding components in
     * the current model added with {@code toAdd}.<br>
     * 5. Browser url and selected card remain unchanged.<br>
     * 6. Status bar's sync status changes.<br>
     * Verifications 1, 3 and 4 are performed by
     * {@code AddressBookSystemTest#assertApplicationDisplaysExpected(String, String, Model)}.<br>
     * @see AddressBookSystemTest#assertApplicationDisplaysExpected(String, String, Model)
     */
    private void assertCommandSuccess(Record toAdd) {
        assertCommandSuccess(RecordUtil.getAddCommand(toAdd), toAdd);
    }

    /**
     * Performs the same verification as {@code assertCommandSuccess(Record)}. Executes {@code command}
     * instead.
     * @see SpendCommandSystemTest#assertCommandSuccess(Record)
     */
    private void assertCommandSuccess(String command, Record toAdd) {
        Model expectedModel = getModel();
        expectedModel.addRecord(toAdd);
        String expectedResultMessage = String.format(SpendCommand.MESSAGE_SUCCESS, toAdd);

        assertCommandSuccess(command, expectedModel, expectedResultMessage);
    }

    /**
     * Performs the same verification as {@code assertCommandSuccess(String, Record)} except asserts that
     * the,<br>
     * 1. Result display box displays {@code expectedResultMessage}.<br>
     * 2. {@code Storage} and {@code PersonListPanel} equal to the corresponding components in
     * {@code expectedModel}.<br>
     * @see SpendCommandSystemTest#assertCommandSuccess(String, Record)
     */
    private void assertCommandSuccess(String command, Model expectedModel, String expectedResultMessage) {
        executeCommand(command);
        assertApplicationDisplaysExpected("", expectedResultMessage, expectedModel);
        assertSelectedCardUnchanged();
        assertCommandBoxShowsDefaultStyle();
        assertStatusBarUnchangedExceptSyncStatus();
    }

    /**
     * Executes {@code command} and asserts that the,<br>
     * 1. Command box displays {@code command}.<br>
     * 2. Command box has the error style class.<br>
     * 3. Result display box displays {@code expectedResultMessage}.<br>
     * 4. {@code Storage} and {@code PersonListPanel} remain unchanged.<br>
     * 5. Browser url, selected card and status bar remain unchanged.<br>
     * Verifications 1, 3 and 4 are performed by
     * {@code AddressBookSystemTest#assertApplicationDisplaysExpected(String, String, Model)}.<br>
     * @see AddressBookSystemTest#assertApplicationDisplaysExpected(String, String, Model)
     */
    private void assertCommandFailure(String command, String expectedResultMessage) {
        Model expectedModel = getModel();

        executeCommand(command);
        assertApplicationDisplaysExpected(command, expectedResultMessage, expectedModel);
        assertSelectedCardUnchanged();
        assertCommandBoxShowsErrorStyle();
        assertStatusBarUnchanged();
    }
}
