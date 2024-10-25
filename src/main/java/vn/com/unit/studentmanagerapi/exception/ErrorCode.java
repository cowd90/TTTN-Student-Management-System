package vn.com.unit.studentmanagerapi.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;


@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
	/* Common Errors */
	UNCATEGORIZED_EXCEPTION("UNCATEGORIZED_EXCEPTION",
			"Uncategorized error. Please try again", HttpStatus.INTERNAL_SERVER_ERROR),
	UNAUTHENTICATED("UNAUTHENTICATED", "Unauthenticated", HttpStatus.UNAUTHORIZED),
	UNAUTHORIZED("UNAUTHORIZED", "You do not have permission", HttpStatus.FORBIDDEN),
	INVALID_KEY("INVALID_KEY", "Invalid message key"),
	STU_NOT_EXIST("STU_NOT_EXIST", "Student does not exist"),
	EMAIL_PASSWORD_INCORRECT("EMAIL_PASSWORD_INCORRECT",
			"Email or password is incorrect", HttpStatus.UNAUTHORIZED),
	PAGE_SIZE_INVALID("STU_SEARCH_PAGE_SIZE_INVALID", "Page and size must be positive"),

	/* STUDENT ERRORS */
	// Full Name Errors
	FULL_NAME_NULL_EMPTY("STU_FULL_NAME_NULL_EMPTY", "Full name cannot be null or empty"),
	FULL_NAME_TOO_LONG("STU_FULL_NAME_TOO_LONG", "Full name cannot exceed {max} characters"),
	FULL_NAME_INVALID("STU_FULL_NAME_INVALID", "Full name must contains only letters"),

	// Avatar Errors
	AVATAR_NULL("STU_AVATAR_NULL", "Avatar cannot be null"),
	AVATAR_TOO_LARGE("STU_AVATAR_TOO_LARGE", "Avatar file size must be less than size"),
	AVATAR_UNSUPPORTED_FORMAT("STU_AVATAR_UNSUPPORTED_FORMAT",
			"Avatar must be in .jpg, .png, .jpeg, .webp, .svg, .psd format",
			HttpStatus.UNSUPPORTED_MEDIA_TYPE
	),

	// Email Errors
	EMAIL_NULL_EMPTY("EMAIL_NULL_EMPTY", "Email cannot be null or empty"),
	EMAIL_TOO_LONG("EMAIL_TOO_LONG", "Email must be less than {max} characters"),
	EMAIL_INVALID_FORMAT("EMAIL_INVALID_FORMAT", "Email format is invalid"),

	// Date of Birth Errors
	DOB_NULL("STU_DOB_NULL", "Date of birth cannot be null"),
	DATE_INVALID_FORMAT("STU_DOB_INVALID_FORMAT", "Date must be in yyyy-MM-dd format"),
	DOB_IN_FUTURE("STU_DOB_IN_FUTURE", "Date of birth cannot be in the future"),

	// Gender Errors
	GENDER_NULL("STU_GENDER_NULL", "Gender cannot be null"),
	GENDER_INVALID("STU_GENDER_INVALID", "Gender must be 'Male', 'Female', or 'Other'"),

	// Date of Admission Errors
	DOA_NULL("STU_DOA_NULL", "Date of admission cannot be null"),
	DOA_BEFORE_DOB("STU_DOA_BEFORE_DOB", "Date of admission cannot be before date of birth"),

	/* ACCOUNT ERRORS */
	ACCOUNT_EXISTS("ACC_EXIST", "Email exists"),
	ACCOUNT_NOT_EXISTS("ACCOUNT_NOT_EXISTS", "Email is not signed up yet"),
	PASSWORD_NULL_EMPTY("PASSWORD_NULL_EMPTY", "Password cannot be null or empty"),
	PASSWORD_INVALID("ACC_PASSWORD_INVALID", "Password must not contain accented characters or space"),
	PASSWORD_LENGTH_INVALID("PASSWORD_LENGTH_INVALID",
			"Password must be at least {min} and maximum of {max} characters"),

	/* SUBJECT ERRORS */
	// Subject Name Errors
	SUBJECT_NAME_IS_EMPTY("SUB_NAME_IS_EMPTY",
			"Subject name cannot be empty", HttpStatus.BAD_REQUEST),
	SUBJECT_NAME_TOO_SHORT("SUB_NAME_TOO_SHORT",
			"Subject name must be at least characters", HttpStatus.BAD_REQUEST),
	SUBJECT_NAME_TOO_LONG("SUB_NAME_TOO_LONG",
			"Subject name must be less than or equal to characters", HttpStatus.BAD_REQUEST),
	SUBJECT_NAME_CONTAINING_SPECIAL_CHARACTERS("SUB_NAME_CONTAINING_SPECIAL_CHARACTERS",
			"Subject name contains special characters"),

	// Start Date Errors
	START_DATE_INVALID("SUB_START_DATE_INVALID", "Start date must be a valid date"),
	START_DATE_FORMAT("SUB_START_DATE_FORMAT", "Start date must be in the format {format}"),
	START_DATE_NULL("SUB_START_DATE_NULL",
			"Start date cannot be null", HttpStatus.BAD_REQUEST),
	DATE_INVALID("DATE_INVALID",
			"Date must be a valid date", HttpStatus.BAD_REQUEST),
	START_DATE_ERROR_FORMAT("SUB_START_DATE_ERROR_FORMAT",
			"Start date must be in the format {format}", HttpStatus.BAD_REQUEST),
	START_DATE_BEFORE_CURRENT_DATE("SUB_START_DATE_BEFORE_CURRENT_DATE",
			"Start date must be after the current date"),

	// End Date Errors
	END_DATE_INVALID("SUB_END_DATE_INVALID", "End date must be a valid date"),
	END_DATE_BEFORE_CURRENT_DATE("SUB_END_DATE_BEFORE_CURRENT_DATE",
			"End date must be after than the current date"),
	END_DATE_NULL("SUB_END_DATE_NULL",
			"End date cannot be null", HttpStatus.BAD_REQUEST),
	END_DATE_FORMAT("SUB_END_DATE_FORMAT",
			"End date must be in the format {format}", HttpStatus.BAD_REQUEST),
	END_DATE_BEFORE_START_DATE("END_DATE_BEFORE_START_DATE",
			"End date must be after the start date"),

	// Number of Students Errors
	// Number of Credits Errors
	NUMBER_OF_STUDENT_NULL(
			"SUB_NUMBER_OF_STUDENT_NULL",
			"Number of students cannot be null",
			HttpStatus.BAD_REQUEST
	),
	NUMBER_OF_STUDENT_POSITIVE_INT(
			"SUB_NUMBER_OF_STUDENT_POSITIVE_INT",
			"Number of students must be a positive integer",
			HttpStatus.BAD_REQUEST
	),
	NUMBER_OF_STUDENTS_EXCEEDS_LIMIT(
			"SUB_NUMBER_OF_STUDENTS_EXCEEDS_LIMIT",
			"Number of students must be less than or equal to",
			HttpStatus.BAD_REQUEST
	),


	// Number of Credits Errors
	NUMBER_OF_CREDIT_NULL(
			"SUB_NUMBER_OF_CREDIT_NULL",
			"Number of credits cannot be null",
			HttpStatus.BAD_REQUEST
	),
	NUMBER_OF_CREDIT_POSITIVE_INT(
			"SUB_NUMBER_OF_CREDIT_POSITIVE_INT",
			"Number of credits must be a positive integer",
			HttpStatus.BAD_REQUEST
	),
	NUMBER_OF_CREDIT_EXCEEDS_LIMIT(
			"SUB_NUMBER_OF_CREDIT_EXCEEDS_LIMIT",
			"Number of credits must be less than or equal to",
			HttpStatus.BAD_REQUEST
	),

	// Course ID Errors
	COURSE_ID_NULL("SUB_COURSE_ID_NULL", "Course ID cannot null"),
	COURSE_ID_INVALID("SUB_COURSE_ID_INVALID", "Course ID must be 'K01' or 'K02'"),

	// Tuition Errors
	TUITION_POSITIVE_INT("SUB_TUITION_POSITIVE_INT", "Tuition must be a positive integer"),
	TUITION_NULL("SUB_TUITION_NULL",
			"Tuition cannot null", HttpStatus.BAD_REQUEST),
	TUITION_TOO_SMALL("SUB_TUITION_TOO_SMALL",
			"Tuition must be at least", HttpStatus.BAD_REQUEST),
	TUITION_TOO_BIG("SUB_TUITION_TOO_BIG",
			"Tuition must be less than or equal to", HttpStatus.BAD_REQUEST),
	// Description Errors
	DESC_EMPTY("SUB_DESC_EMPTY", "Description cannot be empty"),

	SUBJECT_NOT_FOUND("SUB_NOT_FOUND", "Not found subject by subject id", HttpStatus.NOT_FOUND),
	/* REGISTRATION SUBJECT ERRORS */

	// Subject ID Errors

	// Student ID Errors

	// Course-related Errors
	SUBJECT_ID_NULL("REG_SUB_ID_NULL",
			"Subject ID cannot be null", HttpStatus.BAD_REQUEST),
	SUBJECT_ID_NOT_POSITIVE("REG_SUB_ID_NOT_POSITIVE",
			"Subject ID must be a positive number", HttpStatus.BAD_REQUEST),
	SUBJECT_ID_INVALID("REG_SUB_ID_INVALID",
			"Subject ID is invalid or does not identify any object", HttpStatus.NOT_FOUND),

	// Student ID Errors
	STU_ID_NULL("REG_STU_ID_NULL",
			"Student ID cannot be null", HttpStatus.BAD_REQUEST),
	STU_ID_NOT_POSITIVE("REG_STU_ID_NOT_POSITIVE",
			"Student ID must be a positive number", HttpStatus.BAD_REQUEST),
	STU_ID_INVALID("REG_STU_ID_INVALID",
			"Student ID is invalid or does not identify any object", HttpStatus.NOT_FOUND),

	// Course-related Errors
	COURSE_CLOSED_FOR_REGISTRATION("REG_COURSE_CLOSED",
			"The course is closed for student registration", HttpStatus.NOT_ACCEPTABLE),
	EXCEEDS_MAXIMUM_SUBJECTS("REG_EXCEEDS_MAX_SUBJECTS",
			"Exceeds the maximum of 5 subjects allowed per course or the maximum number of students already registered for the subject.", HttpStatus.NOT_ACCEPTABLE),

	STU_ALREADY_REGISTERED("STUDENT_ALREADY_REGISTERED", "Student has already registered for this course.", HttpStatus.NOT_ACCEPTABLE)
	;
	ErrorCode(String code, String message, HttpStatus httpStatus) {
		this.code = code;
		this.message = message;
		this.httpStatus = httpStatus;
	}

	ErrorCode(String code, String message) {
		this.code = code;
		this.message = message;
		this.httpStatus = HttpStatus.BAD_REQUEST;
	}

	String code;
	String message;
	HttpStatus httpStatus;

}
