(set-env!
 :source-paths #{"src"}
 :dependencies '[[org.clojure/clojure "1.8.0"]])

(task-options!
 sift {:include #{#"\.jar$"}}
 pom {:project 'bootfxui
      :version "0.1.0-SNAPSHOT"}
 aot {:namespace '#{bootfxui.core}}
 jar {:main 'bootfxui.core
      :manifest {"Description" "A simple GUI with boot and JavaFX"}
      :file "project.jar"})

(deftask build []
  (comp (aot) (pom) (uber) (jar) (sift) (target))
  )
