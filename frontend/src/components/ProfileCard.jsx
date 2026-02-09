import {useNavigate} from "react-router-dom";
import styles from "../styles/components/ProfileCard.module.css"

export const ProfileCard = ({ userProfile, teamProfile, isUser = false, isTeam = false }) => {
    const navigate = useNavigate();

    const handleNavigate = () => {
        if(isUser) {
            navigate("/profile")
        } else if(isTeam) {
            navigate("/team/profile")
        }
    }

    const defaultPfpImage = "/assets/defaultPFP.jpg";

    return (
        <div className={styles["profile-card"]}>
            <div onClick={() => handleNavigate()}>
                {isUser ?
                    <img className={styles["profile-avatar"]} src={defaultPfpImage} alt="username"/>
                    :
                    <img className={styles["profile-avatar"]} src={defaultPfpImage} alt="team name"/>
                }
            </div>
            <div className={styles["profile-name"]}>
                {isUser ?
                    <p>defUsername</p>
                    :
                    <p>defTeamname</p>
                }
            </div>
        </div>
    );
}

// TODO: remove hardcoded pfp and alt