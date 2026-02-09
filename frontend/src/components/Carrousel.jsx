import styles from "../styles/components/Carrousel.module.css"
import {useEffect, useRef, useState} from "react";
import {ProfileCard} from "./ProfileCard.jsx";
import {ElementCard} from "./ElementCard.jsx";

export const Carrousel = ({ elements, isGame = false, isUserProfile = false, isTeamProfile = false }) => {
    const [currentIndex, setCurrentIndex] = useState(0);
    const carrouselRef = useRef(null);

    useEffect(() => {
        const totalItems = elements.length;
        setCurrentIndex(0);
    }, [elements]);

    const elementsPerView = 3;

    const moveCarrousel = (direction) => {
        const totalElements = elements.length;
        let newIndex = currentIndex + direction * elementsPerView;
        newIndex = (totalElements + newIndex) % totalElements;
        while(newIndex % elementsPerView !== 0) {
            newIndex -= direction;
        }
        setCurrentIndex(newIndex);

        if(carrouselRef.current) {
            const step = 100 / elementsPerView;
            carrouselRef.current.style.transform = `translateX(${-newIndex * step}%)`;
            carrouselRef.current.style.transition = "transform 0.3s ease";
        }
    }

    const arrowImage = "/assets/arrow.png";
    const defaultElementImage = "/assets/arcane.jpg";

    return (
    <div className={styles.container}>
        <button className={`${styles.arrow} ${styles["arrow-left"]}`} onClick={() => moveCarrousel(Direction.LEFT)}>
            <img src={arrowImage} alt="Previous" className={`${styles["arrow-icon"]} ${styles["arrow-left"]}`}/>
        </button>

        <div className={styles.carrousel} ref={carrouselRef}>
            <div className={styles.track}>
                {elements.map((element) => {
                    if(isUserProfile) {
                        return <ProfileCard isUser={true}/>;
                    } else if(isTeamProfile) {
                        return <ProfileCard isTeam={true}/>;
                    } else {
                        return (
                            <div className={styles["carrousel-item"]}>
                                <ElementCard
                                    image={defaultElementImage}
                                    title={element.name}
                                    started={isGame ? null : element.tournamentStarted}
                                    finished={isGame ? null : element.isFinished}
                                    id={element.id}
                                    isGame={isGame}/>
                            </div>
                        );
                    }
                })}
            </div>
        </div>

        <button className={`${styles.arrow} ${styles["arrow-right"]}`} onClick={() => moveCarrousel(Direction.RIGHT)}>
            <img src={arrowImage} alt="Next" className={`${styles["arrow-icon"]} ${styles["arrow-right"]}`}/>
        </button>
    </div>
    );
}

const Direction = {
    LEFT: -1,
    RIGHT: 1,
}