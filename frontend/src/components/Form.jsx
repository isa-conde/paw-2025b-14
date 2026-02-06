import styles from "../styles/components/Form.module.css"

export const Form = ({ method, action, encType, children }) => {
    return (
      <form method={method} action={action} encType={encType} className={styles.container}>
          {children}
      </form>
    );
}