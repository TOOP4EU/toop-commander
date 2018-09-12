package eu.toop.commander.exceptions;

public class ErrorReportingException extends IllegalStateException {
  public ErrorReportingException(Throwable e) {
    super(e);
  }
}
