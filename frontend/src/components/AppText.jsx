import styles from "../styles/components/Text.module.css";

export const AppText = ({ type, size, weight, stroke, children }) => {
  const textType = type || "main-text";
  const fontSize = size || "m";
  const fontWeight = weight || "bold";
  const strokeClass = stroke ? styles["text-stroke"] : "";

  const className = `${styles[textType]} ${styles[fontSize]} ${styles[fontWeight]} ${strokeClass}`;

  return <p className={className}> {children} </p>;
};
