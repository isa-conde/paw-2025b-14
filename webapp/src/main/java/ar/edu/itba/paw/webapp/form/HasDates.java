package ar.edu.itba.paw.webapp.form;

import java.time.LocalDate;

public interface HasDates {
    LocalDate getStart_date();
    LocalDate getEnd_date();
}
