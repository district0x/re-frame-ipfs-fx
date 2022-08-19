# re-frame-ipfs-fx

[![CircleCI](https://dl.circleci.com/status-badge/img/gh/district0x/re-frame-ipfs-fx/tree/master.svg?style=svg)](https://dl.circleci.com/status-badge/redirect/gh/district0x/re-frame-ipfs-fx/tree/master)

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

# Develop, test, release

## Browser

1. Build: `npx shadow-cljs watch test-browser`
2. Tests: http://d0x-vm:6502

## CI (Headless Chrome, Karma)

1. Build: `npx shadow-cljs compile test-ci`
2. Tests:
    ```
    CHROME_BIN=`which chromium-browser` npx karma start karma.conf.js --single-run
    ```

#### inspect on headless chrome on another chrome instance

1. Run headless chrome: `chromium-browser --headless --remote-debugging-port=9222 --remote-debugging-address=0.0.0.0 --allowed-origins="*" https://chromium.org`
2. Open `chrome://inspect/#devices` and configure remote target with *IP ADDRESS* (hostname doesn't work)

## Build & release with `deps.edn` and `tools.build`

1. Build: `clj -T:build jar`
2. Release: `clj -T:build deploy`
