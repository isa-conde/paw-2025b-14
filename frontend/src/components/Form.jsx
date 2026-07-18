import styles from "../styles/components/Form.module.css"

export const Form = ({ method, action, encType, onSubmit, children }) => {
    return (
      <form method={method} action={action} encType={encType} onSubmit={onSubmit} className={styles.container}>
          {children}
      </form>
    );
}
