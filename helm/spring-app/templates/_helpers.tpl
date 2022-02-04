{{- define "zookeeper.url" -}}
{{- printf  .Release.Name "-cp-zookeeper-0." .Release.Name "-cp-zookeeper-headless:2181" -}}
{{- end -}}