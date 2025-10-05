package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.TournamentDao;
import ar.edu.itba.paw.interfaces.services.UpdateDatesService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateDatesServiceImpl implements UpdateDatesService {

    private static final String TZ = "America/Argentina/Buenos_Aires";
    private final TournamentDao tournamentDao;

    public UpdateDatesServiceImpl(TournamentDao tournamentDao) {
        this.tournamentDao = tournamentDao;
    }

    @Transactional
    @Override
    @Scheduled(cron = "0 10 3 * * *", zone = TZ)
    public void updateDates() {
        tournamentDao.updateAllStartDates();
        tournamentDao.updateAllEndDates();
    }
}
