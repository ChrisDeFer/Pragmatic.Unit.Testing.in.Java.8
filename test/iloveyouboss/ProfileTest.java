package iloveyouboss;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class ProfileTest {

	private Profile profile;
	private Criteria criteria;

	private Question questionReimbursesTuition;
	private Answer answerReimbursesTuition;
	private Answer answerDoesNotReimburseTuition;

	private Question questionIsThereRelocation;
	private Answer answerThereIsRelocation;
	private Answer answerThereIsNoRelocation;

	private Question questionOnsiteDaycare;
	private Answer answerNoOnsiteDaycare;
	private Answer answerHasOnsiteDaycare;

	@Before
	// Remember setup is run before each test
	public void createProfile() {
		profile = new Profile("Test Inc");
	}

	@Before
	public void createCriteria() {
		criteria = new Criteria();
	}

	@Before
	public void createQuestionsAndAnswers() {
		questionIsThereRelocation = new BooleanQuestion(1, "Relocation package?");
		answerThereIsRelocation = new Answer(questionIsThereRelocation, Bool.TRUE);
		answerThereIsNoRelocation = new Answer(questionIsThereRelocation, Bool.FALSE);

		questionReimbursesTuition = new BooleanQuestion(1, "Reimburses tuition?");
		answerReimbursesTuition = new Answer(questionReimbursesTuition, Bool.TRUE);
		answerDoesNotReimburseTuition = new Answer(questionReimbursesTuition, Bool.FALSE);

		questionOnsiteDaycare = new BooleanQuestion(1, "Onsite daycare?");
		answerHasOnsiteDaycare = new Answer(questionOnsiteDaycare, Bool.TRUE);
		answerNoOnsiteDaycare = new Answer(questionOnsiteDaycare, Bool.FALSE);
	}

	@Test
	public void matchAnswersFalseWhenMustMatchCriteriaNotMet() {
		// Arrange
		profile.add(new Answer(questionReimbursesTuition, Bool.TRUE));
		criteria.add(new Criterion(new Answer(questionReimbursesTuition, Bool.FALSE), Weight.MustMatch));

		// Act & Asset
		assertThat(profile.matches(criteria), equalTo(false));
	}

	@Test
	public void matchAnswersTrueForAnyDontCareCriteria() {
		profile.add(new Answer(questionReimbursesTuition, Bool.TRUE));
		criteria.add(new Criterion(new Answer(questionReimbursesTuition, Bool.FALSE), Weight.DontCare));

		assertThat(profile.matches(criteria), equalTo(true));
	}

	@Test
	public void matchAnswersTrueWhenAnyOfMultipleCriteriaMatch() {
		profile.add(new Answer(questionReimbursesTuition, Bool.TRUE));
		criteria.add(new Criterion(new Answer(questionReimbursesTuition, Bool.TRUE), Weight.Important));
		profile.add(new Answer(questionIsThereRelocation, Bool.FALSE));
		criteria.add(new Criterion(new Answer(questionIsThereRelocation, Bool.TRUE), Weight.WouldPrefer));

		assertThat(profile.matches(criteria), equalTo(true));
	}

	@Test
	public void matchAnswersFalseWhenNoneOfMultipleCriteriaMatch() {
		profile.add(new Answer(questionReimbursesTuition, Bool.FALSE));
		criteria.add(new Criterion(new Answer(questionReimbursesTuition, Bool.TRUE), Weight.Important));
		profile.add(new Answer(questionIsThereRelocation, Bool.FALSE));
		criteria.add(new Criterion(new Answer(questionIsThereRelocation, Bool.TRUE), Weight.Important));

		assertThat(profile.matches(criteria), equalTo(false));
	}

	@Test
	public void scoreIsZeroWhenThereAreNoMatches() {
		profile.add(new Answer(questionReimbursesTuition, Bool.FALSE));
		criteria.add(new Criterion(new Answer(questionReimbursesTuition, Bool.TRUE), Weight.Important));
		profile.add(new Answer(questionIsThereRelocation, Bool.FALSE));
		criteria.add(new Criterion(new Answer(questionIsThereRelocation, Bool.TRUE), Weight.Important));

		profile.matches(criteria);
		assertThat(profile.score(), equalTo(0));
	}

	@Test
	public void scoreIsCriterionValueForSingleMatch() {
		profile.add(new Answer(questionReimbursesTuition, Bool.FALSE));
		criteria.add(new Criterion(new Answer(questionReimbursesTuition, Bool.TRUE), Weight.Important));
		profile.add(new Answer(questionIsThereRelocation, Bool.TRUE));
		criteria.add(new Criterion(new Answer(questionIsThereRelocation, Bool.TRUE), Weight.Important));

		profile.matches(criteria);
		assertThat(profile.score(), equalTo(Weight.Important.getValue()));
	}

	@Test
	public void scoreAccumulatesCriterionValuesForMatches() {
		profile.add(new Answer(questionReimbursesTuition, Bool.TRUE));
		criteria.add(new Criterion(new Answer(questionReimbursesTuition, Bool.TRUE), Weight.Important));
		profile.add(new Answer(questionIsThereRelocation, Bool.TRUE));
		criteria.add(new Criterion(new Answer(questionIsThereRelocation, Bool.TRUE), Weight.WouldPrefer));

		int expectedScore = Weight.Important.getValue() + Weight.WouldPrefer.getValue();
		profile.matches(criteria);
		assertThat(profile.score(), equalTo(expectedScore));
	}

}
