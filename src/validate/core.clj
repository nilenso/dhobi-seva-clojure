(ns validate.core
  (:gen-class))

(defn date-validator
	[date]
	(re-find #"^[0-9][0-9][0-9][0-9]-((0[1-9])|(1[012]))-((0[1-9])|(1[0-9])|(2[0-9])|(3[01]))$" date))

(defn duration-validator
	[duration]
	(re-find #"^([1-9]|([1-9][0-9]))$" duration))

(defn is-empty?
	[& args]
	(some empty? args))

(defn integer-validator
	[duration]
	(re-find #"^[1-9][0-9]*$" duration))
