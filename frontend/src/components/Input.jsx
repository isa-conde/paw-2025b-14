import styles from "../styles/components/Input.module.css"
import { AppText } from "./AppText.jsx";

export const Input = ({   id,
                          name,
                          label,
                          type = "input",
                          containerType,
                          inline,
                          value,
                          disabled,
                          placeholder,
                          secondary = false,
                          error,
                          onChange }) => {
    const containerClass = inline
        ? styles["inline-container"]
        : containerType === "half"
        ? styles["half-container"]
        : styles.container

    if(type !== "submit") {
        if(type === "input") {
            return (
                <label htmlFor={id} className={`${styles.label} ${containerClass}`}>
                    {label && (
                        <AppText size="l" weight="semi-bold">{label}</AppText>
                    )}
                    <input
                        id={id}
                        name={name ?? id}
                        type="text"
                        className={styles.input}
                        value={value}
                        placeholder={placeholder}
                        disabled={disabled}
                        onChange={onChange}
                    />
                    {error && <p className={styles.error}>{error}</p>}
                </label>
            )
        } else if(type === "email") {
            return (
                <label htmlFor={id} className={`${styles.label} ${containerClass}`}>
                    {label && (
                        <AppText size="l" weight="semi-bold">{label}</AppText>
                    )}
                    <input
                        id={id}
                        name={name ?? id}
                        type="email"
                        className={styles.input}
                        value={value}
                        placeholder={placeholder}
                        disabled={disabled}
                        onChange={onChange}
                    />
                    {error && <p className={styles.error}>{error}</p>}
                </label>
            )
        } else if(type === "password") {
            return (
                <label htmlFor={id} className={`${styles.label} ${containerClass}`}>
                    {label && (
                        <AppText size="l" weight="semi-bold">{label}</AppText>
                    )}
                    <input
                        id={id}
                        name={name ?? id}
                        type="password"
                        className={styles.input}
                        value={value}
                        placeholder={placeholder}
                        disabled={disabled}
                        onChange={onChange}
                    />
                    {error && <p className={styles.error}>{error}</p>}
                </label>
            )
        }
    } else {
        const submitBtnClassName = [ styles.btn,
            styles.submit,
            secondary
                ? styles.secondary
                : "",
        ].filter(Boolean).join(" ");

        return (
            <div className={`${styles["inline-container"]}} ${styles["submit-container"]}`}>
                <input type="submit" className={submitBtnClassName} value={label}/>
            </div>
        );
    }

}