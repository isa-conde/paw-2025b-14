import styles from "../styles/components/Input.module.css"
import { AppText } from "./AppText.jsx";

export const Input = ({   id,
                          name,
                          label,
                          type = "input",
                          containerType,
                          items,
                          itemMap,
                          itemValue,
                          itemLabel,
                          emptyOption,
                          inline,
                          value,
                          disabled,
                          placeholder,
                          secondary = false,
                          error,
                          min,
                          max,
                          step,
                          onChange }) => {
    const containerClass = inline
        ? styles["inline-container"]
        : containerType === "half"
        ? styles["half-container"]
        : styles.container

    if(type !== "submit") {
        if(["input", "email", "password", "number", "url"].includes(type)) {
            const htmlType = type === "input" ? "text" : type;
            return (
                <label htmlFor={id} className={`${styles.label} ${containerClass}`}>
                    {label && (
                        <AppText size="l" weight="semi-bold">{label}</AppText>
                    )}
                    <input
                        id={id}
                        name={name ?? id}
                        type={htmlType}
                        className={styles.input}
                        value={value}
                        placeholder={placeholder}
                        disabled={disabled}
                        min={min}
                        max={max}
                        step={step}
                        onChange={onChange}
                    />
                    {error && <p className={styles.error}>{error}</p>}
                </label>
            )
        } else if(type === "select") {
            const mapEntries = itemMap instanceof Map
                ? Array.from(itemMap.entries())
                : Object.entries(itemMap ?? {})

            return (
                <label htmlFor={id} className={`${styles.label} ${containerClass}`}>
                    {label && (
                        <AppText size="l" weight="semi-bold">{label}</AppText>
                    )}
                    <select
                        id={id}
                        name={name ?? id}
                        className={styles.input}
                        value={value}
                        disabled={disabled}
                        onChange={onChange}
                    >
                        {emptyOption != null && <option value="">{emptyOption}</option>}

                        {(itemLabel != null && itemValue != null) && items?.map((item) => (
                            <option key={`${item[itemValue]}`} value={item[itemValue]}>
                                {item[itemLabel]}
                            </option>
                        ))}

                        {(itemLabel == null || itemValue == null) && itemMap != null && mapEntries.map(([key, optionLabel]) => (
                            <option key={`${key}`} value={key}>
                                {optionLabel}
                            </option>
                        ))}

                        {(itemLabel == null || itemValue == null) && itemMap == null && items?.map((item) => (
                            <option key={`${item}`} value={item}>
                                {item}
                            </option>
                        ))}
                    </select>
                    {error && <p className={styles.error}>{error}</p>}
                </label>
            )
        } else if(type === "date") {
            return(
                <label htmlFor={id} className={`${styles.label} ${containerClass}`}>
                    {label && (
                        <AppText size="l" weight="semi-bold">{label}</AppText>
                    )}
                    <input
                        id={id}
                        name={name ?? id}
                        type="date"
                        className={styles.input}
                        value={value}
                        disabled={disabled}
                        min={min}
                        max={max}
                        onChange={onChange}
                    />
                    {error && <p className={styles.error}>{error}</p>}
                </label>
            );
        }
    } else {
        const submitBtnClassName = [ styles.btn,
            styles.submit,
            secondary
                ? styles.secondary
                : "",
        ].filter(Boolean).join(" ");

        return (
            <div className={`${styles["inline-container"]} ${styles["submit-container"]}`}>
                <input type="submit" className={submitBtnClassName} value={label} disabled={disabled}/>
            </div>
        );
    }

}
