import { useContext } from "react";
import {Button} from "./Button.jsx";
import { AuthContext } from "../auth/AuthContext.jsx";

export const ProfileButton  = ({ text, size = "m", onClick, imageId, fill = true, disabled = false, isNotSafe, rating }) => {
    const auth = useContext(AuthContext);
    const user = auth?.user;
    const defaultImage = "/assets/defaultPFP.jpg"

    return (
        <Button
            text={text ?? user?.username}
            onClick={onClick}
            image={defaultImage}
            disabled={disabled}
            size={size}
            fill={fill}
            isNotSafe={isNotSafe}
            rating={rating ?? user?.rating}
            />
    );
}

// TODO: imageId not used, passed image is hardcoded to default
