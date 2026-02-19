import styles from "../styles/pages/NewTournamentPage.module.css"
import {useTranslation} from "react-i18next";
import {useState} from "react";

const Steps = {
    STEP_1: 1,
    STEP_2: 2,
}

export const NewTournamentPage = () => {
    const { t } = useTranslation();
    const [currentStep, setCurrentStep] = useState()
}

const Step1Form = () => {

}