from flask import Flask, render_template, request, redirect, url_for, session, flash
from werkzeug.security import generate_password_hash, check_password_hash

app = Flask(__name__)
app.secret_key = 'clave_super_secreta'

# Simulación de base de datos en memoria con usuario de prueba
usuarios = {
    'testuser': generate_password_hash('123')
}
tareas = {
    'testuser': []
}

@app.route('/')
def home():
    return render_template('home.html')

@app.route('/register', methods=['GET', 'POST'])
def register():
    if request.method == 'POST':
        usuario = request.form['username'].strip()
        clave = request.form['password'].strip()

        if not usuario or not clave:
            flash("Usuario y contraseña no pueden estar vacíos ni contener solo espacios.", "error")
            return redirect(url_for('register'))

        if usuario in usuarios:
            flash("El usuario ya existe", "error")
            return redirect(url_for('register'))

        usuarios[usuario] = generate_password_hash(clave)
        tareas[usuario] = []
        flash("Registro exitoso", "success")
        return redirect(url_for('login'))

    return render_template('register.html')


@app.route('/login', methods=['GET', 'POST'])
def login():
    if request.method == 'POST':
        usuario = request.form['username'].strip()
        clave = request.form['password'].strip()

        if not usuario or not clave:
            flash("Usuario y contraseña no pueden estar vacíos ni contener solo espacios.", "error")
            return redirect(url_for('login'))

        if usuario in usuarios and check_password_hash(usuarios[usuario], clave):
            session['usuario'] = usuario
            return redirect(url_for('dashboard'))

        flash("Credenciales incorrectas", "error")
    return render_template('login.html')


@app.route('/dashboard', methods=['GET', 'POST'])
def dashboard():
    if 'usuario' not in session:
        return redirect(url_for('login'))
    usuario = session['usuario']
    if request.method == 'POST':
        nueva_tarea = request.form['task']
        if nueva_tarea:
            tareas[usuario].append(nueva_tarea)
    return render_template('dashboard.html', tareas=tareas[usuario])

@app.route('/logout')
def logout():
    session.pop('usuario', None)
    return redirect(url_for('home'))

@app.route('/delete/<int:task_id>')
def delete_task(task_id):
    usuario = session.get('usuario')
    if usuario and 0 <= task_id < len(tareas[usuario]):
        del tareas[usuario][task_id]
    return redirect(url_for('dashboard'))

if __name__ == '__main__':
    app.run(debug=True)
