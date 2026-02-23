import styles from "../styles/pages/NewTournamentPage.module.css"
import {useTranslation} from "react-i18next";
import {useState} from "react";
import {Layout} from "../components/Layout.jsx";
import {AppText} from "../components/AppText.jsx";
import {Input} from "../components/Input.jsx";

export const NewTournamentPage = () => {
    const { t } = useTranslation();
    const { currentStep, setCurrentStep } = useState(Steps.STEP_1);

    const pageTitle = t("createTournament.pageTitle");
    const emptyOption = t("createTournament.emptyOption");

    const tournamentImage = "/assets/tournament.jpg";

    return (
        <Layout pageTitle={pageTitle}>
            <div className={styles.container}>
                <div className={styles.image}>
                    <img src={tournamentImage} alt="Tournament"/>
                </div>
            </div>
            <div className={styles["tournament-form"]}>
                {currentStep === Steps.STEP_1 ?
                    <Step1Form setCurrentStep={setCurrentStep} emptyOption={emptyOption} t={t}/>
                    :
                    <Step2Form setCurrentStep={setCurrentStep} emptyOption={emptyOption} t={t}/>
                }
            </div>
        </Layout>
    );
};

const Steps = {
    STEP_1: 1,
    STEP_2: 2,
    STEP_3: 3,
};

const Step1Form = ({ setCurrentStep, emptyOption, t }) => {

    const step1Title = t("createTournament.Step1");
    const nameLabel = t("createTournament.name");
    const regionLabel = t("createTournament.region");
    const gameLabel = t("createTournament.game");
    const structureLabel = t("createTournament.structure");
    const startDateLabel = t("createTournament.startDate");
    const endDateLabel = t("createTournament.endDate");
    const nextLabel = t("createTournament.next");

    return (
        <form method="post" className={styles.form} action={setCurrentStep(Steps.STEP_2)}>
            <div>
                <AppText type="title" size="xl">{step1Title}</AppText>
                <div className={styles.row}>
                    <Input id="name" label={nameLabel} name="name"/>
                </div>
                <div className={styles.row}>
                    <Input id="region" label={regionLabel} name="region" type="select" items={regions} emptyOption={emptyOption} itemValue=""/>
                    <Input id="gameId" label={gameLabel} name="game" type="select" items={games} itemValue="id" itemLabel="name" emptyOption={emptyOption}/>
                </div>
                <div className={styles.row}>
                    <Input id="structure" label={structureLabel} type="select" items={structures} emptyOption={emptyOption}/>
                </div>
                <div className={styles.row}>
                    <Input id="startDate" label={startDateLabel} type="date"/>
                    <Input id="endDate" label={endDateLabel} type="date"/>
                </div>
                <div className={`${styles.row} ${styles.center}`}>
                    <Input label={nextLabel} containerType="half" type="submit"/>
                </div>
            </div>
        </form>
    );
}

const Step2Form = ({ setCurrentStep, emptyOption, t }) => {
    const
}

// TODO: add user to layout
// TODO: make the select inputs collect data from API