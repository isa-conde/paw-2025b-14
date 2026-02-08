import {Button} from "./Button.jsx";

export const ProfileButton  = ({ text, size = "m", onClick, imageId, fill = true, disabled = false, isNotSafe, rating }) => {
    const defaultImage = "/assets/defaultPFP.jpg"

    return (
        <Button
            text={text}
            onClick={onClick}
            image={defaultImage}
            disabled={disabled}
            size={size}
            fill={fill}
            isNotSafe={isNotSafe}
            rating={rating}
            />
    );
}

// TODO: imageId not used, passed image is hardcoded to default