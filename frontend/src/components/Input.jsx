import styles from "../styles/Input.module.css"

export const Input = ({ id, name, label, type = "text", containerType, inline, value, disabled, placeholder, error, onChange }) => {
    const containerClass = inline
        ? styles.inlineContainer
        : containerType === "half"
        ? styles.halfContainer
        : styles.container

    if(type === "input" || type === null) {
        return (
            <label htmlFor={id} className={`${styles.label} ${containerClass}`}>
                {label && (
                    <Text size="l" weight="semi-bold">{label}</Text>
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
    }
}