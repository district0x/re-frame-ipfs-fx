# re-frame-ipfs-fx

[![Build Status](https://travis-ci.org/district0x/re-frame-ipfs-fx.svg?branch=master)](https://travis-ci.org/district0x/re-frame-ipfs-fx)

[re-frame](https://github.com/Day8/re-frame) [Effectful Handlers](https://github.com/Day8/re-frame/blob/develop/docs/EffectfulHandlers.md) to work with [IPFS](https://ipfs.io/) network's [HTTP API](https://docs.ipfs.io/reference/api/http/), using [cljs-ipfs-http-client](https://github.com/district0x/cljs-ipfs-http-client)

### Installation ###

Add to dependencies: <br>
[![Clojars Project](https://img.shields.io/clojars/v/district0x/re-frame-ipfs-fx.svg)](https://clojars.org/district0x/re-frame-ipfs-fx)

Using:

```clojure
(ns my.app.handlers
  (:require [district0x.re-frame.ipfs-fx]))
```

## Usage
The library relies on the HTTP API signatures, so follow this [docs](https://github.com/ipfs/js-ipfs-api#api), Also, return values and responses in callbacks are automatically kebab-cased and keywordized. You can provide IPFS instance as an :inst map entry, in case you'd need more than one connection:


### Example call
```clojure
;;Setup your handlers

(reg-event-fx
::init-ipfs
interceptors
(fn [_ [config]]
  {:ipfs/init config}))                                                                     


(reg-event-fx
::list-files
interceptors
(fn [_ [url]]
  {:ipfs/call {:func "ls"
               :args [url]
               :on-success [::on-list-files-success]
               :on-error [::error]}}))  
               
(reg-event-fx
::on-list-files-success
interceptors
(fn [{:keys [:db]} [data]]
  {:db (assoc db :files data)}))

    
(dispatch [::init-ipfs {:host "http://127.0.0.1:5001" :endpoint "/api/v0"}])
(dispatch [::list-files "/ipfs/QmYwAPJzv5CZsnA625s3Xf2nemtYgPpHdWEz79ojWnPbdG/"])
```
