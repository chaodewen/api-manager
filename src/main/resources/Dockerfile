FROM liteapi/pythonrt

COPY . $APP_DIR/
RUN pip install -r requirements.txt

CMD uwsgi --ini $SERVICE_DIR/uwsgi/api_uwsgi.ini