var audioTrack = null;
var vid = null, aud = null;
var h, k = this,
    aa = function(t) {
        var e = typeof t;
        if ("object" == e) {
            if (!t) return "null";
            if (t instanceof Array) return "array";
            if (t instanceof Object) return e;
            var a = Object.prototype.toString.call(t);
            if ("[object Window]" == a) return "object";
            if ("[object Array]" == a || "number" == typeof t.length && "undefined" != typeof t.splice && "undefined" != typeof t.propertyIsEnumerable && !t.propertyIsEnumerable("splice")) return "array";
            if ("[object Function]" == a || "undefined" != typeof t.call && "undefined" != typeof t.propertyIsEnumerable && !t.propertyIsEnumerable("call")) return "function"
        } else if ("function" == e && "undefined" == typeof t.call) return "object";
        return e
    },
    m = function(t) {
        return "array" == aa(t)
    },
    ba = function(t) {
        var e = aa(t);
        return "array" == e || "object" == e && "number" == typeof t.length
    },
    n = function(t) {
        return "string" == typeof t
    },
    ca = function(t) {
        return "function" == aa(t)
    },
    da = function(t) {
        var e = typeof t;
        return "object" == e && null != t || "function" == e
    },
    ea = function(t, e, a) {
        return t.call.apply(t.bind, arguments)
    },
    fa = function(t, e, a) {
        if (!t) throw Error();
        if (2 < arguments.length) {
            var n = Array.prototype.slice.call(arguments, 2);
            return function() {
                var a = Array.prototype.slice.call(arguments);
                return Array.prototype.unshift.apply(a, n), t.apply(e, a)
            }
        }
        return function() {
            return t.apply(e, arguments)
        }
    },
    p = function(t, e, a) {
        return p = Function.prototype.bind && -1 != Function.prototype.bind.toString().indexOf("native code") ? ea : fa, p.apply(null, arguments)
    },
    ga = Date.now || function() {
        return +new Date
    },
    q = function(t, e) {
        function a() {}
        a.prototype = e.prototype, t.Ga = e.prototype, t.prototype = new a, t.prototype.constructor = t, t.Ha = function(t, a, n) {
            for (var i = Array(arguments.length - 2), r = 2; r < arguments.length; r++) i[r - 2] = arguments[r];
            return e.prototype[a].apply(t, i)
        }
    };
Function.prototype.bind = Function.prototype.bind || function(t, e) {
    if (1 < arguments.length) {
        var a = Array.prototype.slice.call(arguments, 1);
        return a.unshift(this, t), p.apply(null, a)
    }
    return p(this, t)
};
var r = function(t) {
    if (Error.captureStackTrace) Error.captureStackTrace(this, r);
    else {
        var e = Error().stack;
        e && (this.stack = e)
    }
    t && (this.message = String(t))
};
q(r, Error), r.prototype.name = "CustomError";
var ha, u = function(e, a) {
        return 0 == t(a, e.substr(0, a.length))
    },
    w = function(e, a) {
        return 0 == t(a, e.substr(e.length - a.length, a.length))
    },
    ia = function(t, e) {
        for (var a = t.split("%s"), n = "", i = Array.prototype.slice.call(arguments, 1); i.length && 1 < a.length;) n += a.shift() + i.shift();
        return n + a.join("%s")
    },
    ja = String.prototype.trim ? function(t) {
        return t.trim()
    } : function(t) {
        return t.replace(/^[\s\xa0]+|[\s\xa0]+$/g, "")
    },
    t = function(t, e) {
        var a = String(t).toLowerCase(),
            n = String(e).toLowerCase();
        return n > a ? -1 : a == n ? 0 : 1
    },
    ra = function(t) {
        return ka.test(t) ? (-1 != t.indexOf("&") && (t = t.replace(la, "&amp;")), -1 != t.indexOf("<") && (t = t.replace(ma, "&lt;")), -1 != t.indexOf(">") && (t = t.replace(na, "&gt;")), -1 != t.indexOf('"') && (t = t.replace(oa, "&quot;")), -1 != t.indexOf("'") && (t = t.replace(pa, "&#39;")), -1 != t.indexOf("\x00") && (t = t.replace(qa, "&#0;")), t) : t
    },
    la = /&/g,
    ma = /</g,
    na = />/g,
    oa = /"/g,
    pa = /'/g,
    qa = /\x00/g,
    ka = /[\x00&<>"']/,
    x = function(t) {
        t = String(t);
        var e = t.indexOf(".");
        return -1 == e && (e = t.length), e = Math.max(0, 2 - e), Array(e + 1).join("0") + t
    },
    ta = function(t, e) {
        for (var a = 0, n = ja(String(t)).split("."), i = ja(String(e)).split("."), r = Math.max(n.length, i.length), s = 0; 0 == a && r > s; s++) {
            var o = n[s] || "",
                c = i[s] || "",
                l = RegExp("(\\d*)(\\D*)", "g"),
                h = RegExp("(\\d*)(\\D*)", "g");
            do {
                var u = l.exec(o) || ["", "", ""],
                    d = h.exec(c) || ["", "", ""];
                if (0 == u[0].length && 0 == d[0].length) break;
                a = sa(0 == u[1].length ? 0 : parseInt(u[1], 10), 0 == d[1].length ? 0 : parseInt(d[1], 10)) || sa(0 == u[2].length, 0 == d[2].length) || sa(u[2], d[2])
            } while (0 == a)
        }
        return a
    },
    sa = function(t, e) {
        return e > t ? -1 : t > e ? 1 : 0
    },
    ua = function(t, e) {
        e.unshift(t), r.call(this, ia.apply(null, e)), e.shift()
    };
q(ua, r), ua.prototype.name = "AssertionError";
var y = function(t, e, a) {
        if (!t) {
            var n = "Assertion failed";
            if (e) var n = n + (": " + e),
                i = Array.prototype.slice.call(arguments, 2);
            throw new ua("" + n, i || [])
        }
    },
    va = function(t, e) {
        throw new ua("Failure" + (t ? ": " + t : ""), Array.prototype.slice.call(arguments, 1))
    },
    z = Array.prototype,
    wa = z.indexOf ? function(t, e, a) {
        return y(null != t.length), z.indexOf.call(t, e, a)
    } : function(t, e, a) {
        if (a = null == a ? 0 : 0 > a ? Math.max(0, t.length + a) : a, n(t)) return n(e) && 1 == e.length ? t.indexOf(e, a) : -1;
        for (; a < t.length; a++)
            if (a in t && t[a] === e) return a;
        return -1
    },
    xa = z.forEach ? function(t, e, a) {
        y(null != t.length), z.forEach.call(t, e, a)
    } : function(t, e, a) {
        for (var i = t.length, r = n(t) ? t.split("") : t, s = 0; i > s; s++) s in r && e.call(a, r[s], s, t)
    },
    ya = function(t) {
        return z.concat.apply(z, arguments)
    },
    za = function(t) {
        var e = t.length;
        if (e > 0) {
            for (var a = Array(e), n = 0; e > n; n++) a[n] = t[n];
            return a
        }
        return []
    },
    Aa = function(t, e) {
        for (var a in t) e.call(void 0, t[a], a, t)
    },
    Ba = function(t) {
        var e, a = [],
            n = 0;
        for (e in t) a[n++] = e;
        return a
    },
    Ca = function(t) {
        for (var e in t) return !1;
        return !0
    },
    Da = "constructor hasOwnProperty isPrototypeOf propertyIsEnumerable toLocaleString toString valueOf".split(" "),
    Ea = function(t, e) {
        for (var a, n, i = 1; i < arguments.length; i++) {
            n = arguments[i];
            for (a in n) t[a] = n[a];
            for (var r = 0; r < Da.length; r++) a = Da[r], Object.prototype.hasOwnProperty.call(n, a) && (t[a] = n[a])
        }
    },
    Fa = function(t) {
        var e = arguments.length;
        if (1 == e && m(arguments[0])) return Fa.apply(null, arguments[0]);
        for (var a = {}, n = 0; e > n; n++) a[arguments[n]] = !0;
        return a
    };
Fa("area base br col command embed hr img input keygen link meta param source track wbr".split(" "));
var A = function() {
    this.a = "", this.c = Ga, this.b = null
};
A.prototype.toString = function() {
    return "SafeHtml{" + this.a + "}"
};
var Ga = {},
    Ha = function(t) {
        var e = new A;
        e.a = t, e.b = 0
    };
Ha("<!DOCTYPE html>"), Ha("");
var Ia = "StopIteration" in k ? k.StopIteration : {
        message: "StopIteration",
        stack: ""
    },
    Ja = function() {};
Ja.prototype.a = function() {
    throw Ia
}, Ja.prototype.g = function() {
    return this
};
var B = function(t, e) {
    this.b = {}, this.a = [], this.f = this.c = 0;
    var a = arguments.length;
    if (a > 1) {
        if (a % 2) throw Error("Uneven number of arguments");
        for (var n = 0; a > n; n += 2) Ka(this, arguments[n], arguments[n + 1])
    } else if (t) {
        if (t instanceof B) a = t.u(), n = t.v();
        else {
            var a = Ba(t),
                i = [],
                r = 0;
            for (n in t) i[r++] = t[n];
            n = i
        }
        for (i = 0; i < a.length; i++) Ka(this, a[i], n[i])
    }
};
B.prototype.v = function() {
    La(this);
    for (var t = [], e = 0; e < this.a.length; e++) t.push(this.b[this.a[e]]);
    return t
}, B.prototype.u = function() {
    return La(this), this.a.concat()
}, B.prototype.remove = function(t) {
    return C(this.b, t) ? (delete this.b[t], this.c--, this.f++, this.a.length > 2 * this.c && La(this), !0) : !1
};
var La = function(t) {
        if (t.c != t.a.length) {
            for (var e = 0, a = 0; e < t.a.length;) {
                var n = t.a[e];
                C(t.b, n) && (t.a[a++] = n), e++
            }
            t.a.length = a
        }
        if (t.c != t.a.length) {
            for (var i = {}, a = e = 0; e < t.a.length;) n = t.a[e], C(i, n) || (t.a[a++] = n, i[n] = 1), e++;
            t.a.length = a
        }
    },
    Ma = function(t, e) {
        return C(t.b, e) ? t.b[e] : void 0
    },
    Ka = function(t, e, a) {
        C(t.b, e) || (t.c++, t.a.push(e), t.f++), t.b[e] = a
    };
B.prototype.forEach = function(t, e) {
    for (var a = this.u(), n = 0; n < a.length; n++) {
        var i = a[n];
        t.call(e, Ma(this, i), i, this)
    }
}, B.prototype.clone = function() {
    return new B(this)
}, B.prototype.g = function(t) {
    La(this);
    var e = 0,
        a = this.f,
        n = this,
        i = new Ja;
    return i.a = function() {
        if (a != n.f) throw Error("The map has changed since the iterator was created");
        if (e >= n.a.length) throw Ia;
        var i = n.a[e++];
        return t ? i : n.b[i]
    }, i
};
var C = function(t, e) {
        return Object.prototype.hasOwnProperty.call(t, e)
    },
    D;
t: {
    var Na = k.navigator;
    if (Na) {
        var Oa = Na.userAgent;
        if (Oa) {
            D = Oa;
            break t
        }
    }
    D = ""
}
var E = function() {
        return -1 != D.indexOf("Edge")
    },
    Pa = -1 != D.indexOf("Opera") || -1 != D.indexOf("OPR"),
    F = -1 != D.indexOf("Edge") || -1 != D.indexOf("Trident") || -1 != D.indexOf("MSIE"),
    G = !(-1 == D.indexOf("Gecko") || -1 != D.toLowerCase().indexOf("webkit") && !E() || -1 != D.indexOf("Trident") || -1 != D.indexOf("MSIE") || E()),
    Qa = -1 != D.toLowerCase().indexOf("webkit") && !E(),
    Ra = function() {
        var t = D;
        return G ? /rv\:([^\);]+)(\)|;)/.exec(t) : F && E() ? /Edge\/([\d\.]+)/.exec(t) : F ? /\b(?:MSIE|rv)[: ]([^\);]+)(\)|;)/.exec(t) : Qa ? /WebKit\/(\S+)/.exec(t) : void 0
    },
    Sa = function() {
        var t = k.document;
        return t ? t.documentMode : void 0
    },
    Ta = function() {
        if (Pa && k.opera) {
            var t = k.opera.version;
            return ca(t) ? t() : t
        }
        var t = "",
            e = Ra();
        return e && (t = e ? e[1] : ""), F && !E() && (e = Sa(), e > parseFloat(t)) ? String(e) : t
    }(),
    Ua = {},
    H = function(t) {
        return Ua[t] || (Ua[t] = 0 <= ta(Ta, t))
    },
    Va = k.document,
    Wa = Sa(),
    Xa = !Va || !F || !Wa && E() ? void 0 : Wa || ("CSS1Compat" == Va.compatMode ? parseInt(Ta, 10) : 5),
    Za = function(t, e, a, n, i) {
        "number" == typeof i || Ya++, this.f = n || ga(), this.g = t, this.c = e, this.b = a, delete this.a
    };
Za.prototype.a = null;
var Ya = 0,
    $a = function(t) {
        this.g = t, this.a = this.c = this.f = this.b = null
    },
    I = function(t, e) {
        this.name = t, this.value = e
    };
I.prototype.toString = function() {
    return this.name
};
var ab = new I("SHOUT", 1200),
    bb = new I("SEVERE", 1e3),
    cb = new I("WARNING", 900),
    db = new I("INFO", 800),
    eb = new I("CONFIG", 700),
    fb = function(t) {
        return t.f ? t.f : t.b ? fb(t.b) : (va("Root logger has no level set."), null)
    };
$a.prototype.log = function(t, e, a) {
    if (t.value >= fb(this).value)
        for (ca(e) && (e = e()), t = new Za(t, String(e), this.g), a && (t.a = a), a = "log:" + t.c, k.console && (k.console.timeStamp ? k.console.timeStamp(a) : k.console.markTimeline && k.console.markTimeline(a)), k.msWriteProfilerMark && k.msWriteProfilerMark(a), a = this; a;) {
            e = a;
            var n = t;
            if (e.a)
                for (var i = 0, r = void 0; r = e.a[i]; i++) r(n);
            a = a.b
        }
};
var J = function(t, e) {
        t.log(cb, e, void 0)
    },
    K = function(t, e) {
        t.log(db, e, void 0)
    },
    gb = {},
    M = null,
    hb = function() {
        M || (M = new $a(""), gb[""] = M, M.f = eb)
    },
    ib = function(t) {
        hb();
        var e;
        if (!(e = gb[t])) {
            e = new $a(t);
            var a = t.lastIndexOf("."),
                n = t.substr(a + 1),
                a = ib(t.substr(0, a));
            a.c || (a.c = {}), a.c[n] = e, e.b = a, gb[t] = e
        }
        return e
    },
    jb = new function() {
        this.a = ga()
    },
    kb = function(t) {
        this.f = t || "", this.g = jb
    };
kb.prototype.a = !0, kb.prototype.b = !0, kb.prototype.c = !1;
var N = function(t) {
        return 10 > t ? "0" + t : String(t)
    },
    lb = function(t) {
        kb.call(this, t)
    };
q(lb, kb);
var mb = function() {
    this.h = p(this.f, this), this.a = new lb, this.a.b = !1, this.a.c = !1, this.b = this.a.a = !1, this.c = "", this.g = {}
};
mb.prototype.f = function(t) {
    if (!this.g[t.b]) {
        var e;
        e = this.a;
        var a = [];
        if (a.push(e.f, " "), e.b) {
            var n = new Date(t.f);
            a.push("[", N(n.getFullYear() - 2e3) + N(n.getMonth() + 1) + N(n.getDate()) + " " + N(n.getHours()) + ":" + N(n.getMinutes()) + ":" + N(n.getSeconds()) + "." + N(Math.floor(n.getMilliseconds() / 10)), "] ")
        }
        var n = (t.f - e.g.a) / 1e3,
            i = n.toFixed(3),
            r = 0;
        if (1 > n) r = 2;
        else
            for (; 100 > n;) r++, n *= 10;
        for (; 0 < r--;) i = " " + i;
        if (a.push("[", i, "s] "), a.push("[", t.b, "] "), a.push(t.c), e.c && (n = t.a) && a.push("\n", n instanceof Error ? n.message : n.toString()), e.a && a.push("\n"), e = a.join(""), a = nb) switch (t.g) {
            case ab:
                ob(a, "info", e);
                break;
            case bb:
                ob(a, "error", e);
                break;
            case cb:
                ob(a, "warn", e);
                break;
            default:
                ob(a, "debug", e)
        } else this.c += e
    }
};
var pb = null,
    nb = k.console,
    ob = function(t, e, a) {
        t[e] ? t[e](a) : t.log(a)
    },
    qb = function(t) {
        return qb[" "](t), t
    };
qb[" "] = function() {};
var rb = !F || F && (E() || Xa >= 9),
    sb = F && !H("9");
!Qa || H("528"), G && H("1.9b") || F && H("8") || Pa && H("9.5") || Qa && H("528"), G && !H("8") || F && H("9");
var tb = function() {
    this.a = this.a, this.m = this.m
};
tb.prototype.a = !1;
var O = function(t, e) {
    this.type = t, this.a = this.target = e
};
O.prototype.b = function() {};
var ub = function(t, e) {
    if (O.call(this, t ? t.type : ""), this.c = this.state = this.a = this.target = null, t) {
        this.type = t.type, this.target = t.target || t.srcElement, this.a = e;
        var a = t.relatedTarget;
        if (a && G) try {
            qb(a.nodeName)
        } catch (n) {}
        this.state = t.state, this.c = t, t.defaultPrevented && this.b()
    }
};
q(ub, O), ub.prototype.b = function() {
    ub.Ga.b.call(this);
    var t = this.c;
    if (t.preventDefault) t.preventDefault();
    else if (t.returnValue = !1, sb) try {
        (t.ctrlKey || 112 <= t.keyCode && 123 >= t.keyCode) && (t.keyCode = -1)
    } catch (e) {}
};
var vb = "closure_listenable_" + (1e6 * Math.random() | 0),
    wb = 0,
    xb = function(t, e, a, n, i) {
        this.listener = t, this.a = null, this.src = e, this.type = a, this.C = !!n, this.D = i, this.X = ++wb, this.s = this.B = !1
    },
    yb = function(t) {
        t.s = !0, t.listener = null, t.a = null, t.src = null, t.D = null
    },
    zb = function(t) {
        this.src = t, this.a = {}, this.b = 0
    };
zb.prototype.add = function(t, e, a, n, i) {
    var r = t.toString();
    t = this.a[r], t || (t = this.a[r] = [], this.b++);
    var s = Ab(t, e, n, i);
    return s > -1 ? (e = t[s], a || (e.B = !1)) : (e = new xb(e, this.src, r, !!n, i), e.B = a, t.push(e)), e
}, zb.prototype.remove = function(t, e, a, n) {
    if (t = t.toString(), !(t in this.a)) return !1;
    var i = this.a[t];
    return e = Ab(i, e, a, n), e > -1 ? (yb(i[e]), y(null != i.length), z.splice.call(i, e, 1), 0 == i.length && (delete this.a[t], this.b--), !0) : !1
};
var Bb = function(t, e) {
        var a = e.type;
        if (a in t.a) {
            var n, i = t.a[a],
                r = wa(i, e);
            (n = r >= 0) && (y(null != i.length), z.splice.call(i, r, 1)), n && (yb(e), 0 == t.a[a].length && (delete t.a[a], t.b--))
        }
    },
    Cb = function(t, e, a, n, i) {
        return t = t.a[e.toString()], e = -1, t && (e = Ab(t, a, n, i)), e > -1 ? t[e] : null
    },
    Ab = function(t, e, a, n) {
        for (var i = 0; i < t.length; ++i) {
            var r = t[i];
            if (!r.s && r.listener == e && r.C == !!a && r.D == n) return i
        }
        return -1
    },
    Db = "closure_lm_" + (1e6 * Math.random() | 0),
    Eb = {},
    Fb = 0,
    Gb = function(t, e, a, n, i) {
        if (m(e)) {
            for (var r = 0; r < e.length; r++) Gb(t, e[r], a, n, i);
            return null
        }
        if (a = Hb(a), t && t[vb]) t = t.g(e, a, n, i);
        else {
            if (!e) throw Error("Invalid event type");
            var r = !!n,
                s = Ib(t);
            s || (t[Db] = s = new zb(t)), a = s.add(e, a, !1, n, i), a.a || (n = Jb(), a.a = n, n.src = t, n.listener = a, t.addEventListener ? t.addEventListener(e.toString(), n, r) : t.attachEvent(Kb(e.toString()), n), Fb++), t = a
        }
        return t
    },
    Jb = function() {
        var t = Lb,
            e = rb ? function(a) {
                return t.call(e.src, e.listener, a)
            } : function(a) {
                return a = t.call(e.src, e.listener, a), a ? void 0 : a
            };
        return e
    },
    Mb = function(t, e, a, n, i) {
        if (m(e))
            for (var r = 0; r < e.length; r++) Mb(t, e[r], a, n, i);
        else a = Hb(a), t && t[vb] ? t.f(e, a, n, i) : t && (t = Ib(t)) && (e = Cb(t, e, a, !!n, i)) && Nb(e)
    },
    Nb = function(t) {
        if ("number" != typeof t && t && !t.s) {
            var e = t.src;
            if (e && e[vb]) Bb(e.o, t);
            else {
                var a = t.type,
                    n = t.a;
                e.removeEventListener ? e.removeEventListener(a, n, t.C) : e.detachEvent && e.detachEvent(Kb(a), n), Fb--, (a = Ib(e)) ? (Bb(a, t), 0 == a.b && (a.src = null, e[Db] = null)) : yb(t)
            }
        }
    },
    Kb = function(t) {
        return t in Eb ? Eb[t] : Eb[t] = "on" + t
    },
    Pb = function(t, e, a, n) {
        var i = !0;
        if ((t = Ib(t)) && (e = t.a[e.toString()]))
            for (e = e.concat(), t = 0; t < e.length; t++) {
                var r = e[t];
                r && r.C == a && !r.s && (r = Ob(r, n), i = i && !1 !== r)
            }
        return i
    },
    Ob = function(t, e) {
        var a = t.listener,
            n = t.D || t.src;
        return t.B && Nb(t), a.call(n, e)
    },
    Lb = function(t, e) {
        if (t.s) return !0;
        if (!rb) {
            var a;
            if (!(a = e)) t: {
                a = ["window", "event"];
                for (var n, i = k; n = a.shift();) {
                    if (null == i[n]) {
                        a = null;
                        break t
                    }
                    i = i[n]
                }
                a = i
            }
            if (n = a, a = new ub(n, this), i = !0, !(0 > n.keyCode || void 0 != n.returnValue)) {
                t: {
                    var r = !1;
                    if (0 == n.keyCode) try {
                        n.keyCode = -1;
                        break t
                    } catch (s) {
                        r = !0
                    }(r || void 0 == n.returnValue) && (n.returnValue = !0)
                }
                for (n = [], r = a.a; r; r = r.parentNode) n.push(r);
                for (var r = t.type, o = n.length - 1; o >= 0; o--) {
                    a.a = n[o];
                    var c = Pb(n[o], r, !0, a),
                        i = i && c
                }
                for (o = 0; o < n.length; o++) a.a = n[o],
                c = Pb(n[o], r, !1, a),
                i = i && c
            }
            return i
        }
        return Ob(t, new ub(e, this))
    },
    Ib = function(t) {
        return t = t[Db], t instanceof zb ? t : null
    },
    Qb = "__closure_events_fn_" + (1e9 * Math.random() >>> 0),
    Hb = function(t) {
        return y(t, "Listener can not be null."), ca(t) ? t : (y(t.handleEvent, "An object listener must have handleEvent method."), t[Qb] || (t[Qb] = function(e) {
            return t.handleEvent(e)
        }), t[Qb])
    },
    Rb = !F || F && (E() || Xa >= 9);
!G && !F || F && F && (E() || Xa >= 9) || G && H("1.9.1"), F && H("9");
var Tb = function(t, e) {
        Aa(e, function(e, a) {
            "style" == a ? t.style.cssText = e : "class" == a ? t.className = e : "for" == a ? t.htmlFor = e : a in Sb ? t.setAttribute(Sb[a], e) : 0 == a.lastIndexOf("aria-", 0) || 0 == a.lastIndexOf("data-", 0) ? t.setAttribute(a, e) : t[a] = e
        })
    },
    Sb = {
        cellpadding: "cellPadding",
        cellspacing: "cellSpacing",
        colspan: "colSpan",
        frameborder: "frameBorder",
        height: "height",
        maxlength: "maxLength",
        role: "role",
        rowspan: "rowSpan",
        type: "type",
        usemap: "useMap",
        valign: "vAlign",
        width: "width"
    },
    Vb = function(t, e, a) {
        function i(a) {
            a && e.appendChild(n(a) ? t.createTextNode(a) : a)
        }
        for (var r = 2; r < a.length; r++) {
            var s = a[r];
            !ba(s) || da(s) && 0 < s.nodeType ? i(s) : xa(Ub(s) ? za(s) : s, i)
        }
    },
    Wb = function(t) {
        return y(t, "Node cannot be null or undefined."), 9 == t.nodeType ? t : t.ownerDocument || t.document
    },
    Ub = function(t) {
        if (t && "number" == typeof t.length) {
            if (da(t)) return "function" == typeof t.item || "string" == typeof t.item;
            if (ca(t)) return "function" == typeof t.item
        }
        return !1
    },
    Xb = function(t) {
        this.b = t || k.document || document
    };
Xb.prototype.a = function(t, e, a) {
    var i = this.b,
        r = arguments,
        s = r[0],
        o = r[1];
    if (!Rb && o && (o.name || o.type)) {
        if (s = ["<", s], o.name && s.push(' name="', ra(o.name), '"'), o.type) {
            s.push(' type="', ra(o.type), '"');
            var c = {};
            Ea(c, o), delete c.type, o = c
        }
        s.push(">"), s = s.join("")
    }
    return s = i.createElement(s), o && (n(o) ? s.className = o : m(o) ? s.className = o.join(" ") : Tb(s, o)), 2 < r.length && Vb(i, s, r), s
};
var Yb = function(t) {
    tb.call(this), this.b = t, this.c = {}
};
q(Yb, tb);
var Zb = [];
Yb.prototype.g = function(t, e, a, n) {
    m(e) || (e && (Zb[0] = e.toString()), e = Zb);
    for (var i = 0; i < e.length; i++) {
        var r = Gb(t, e[i], a || this.handleEvent, n || !1, this.b || this);
        if (!r) break;
        this.c[r.X] = r
    }
    return this
}, Yb.prototype.f = function(t, e, a, n, i) {
    if (m(e))
        for (var r = 0; r < e.length; r++) this.f(t, e[r], a, n, i);
    else a = a || this.handleEvent, i = i || this.b || this, a = Hb(a), n = !!n, e = t && t[vb] ? Cb(t.o, String(e), a, n, i) : t && (t = Ib(t)) ? Cb(t, e, a, n, i) : null, e && (Nb(e), delete this.c[e.X]);
    return this
}, Yb.prototype.handleEvent = function() {
    throw Error("EventHandler.handleEvent not implemented")
};
var P = function() {
    tb.call(this), this.o = new zb(this), this.l = this
};
q(P, tb), P.prototype[vb] = !0, P.prototype.removeEventListener = function(t, e, a, n) {
    Mb(this, t, e, a, n)
};
var bc = function(t, e) {
    $b(t);
    var a = t.l,
        i = e,
        r = i.type || i;
    if (n(i)) i = new O(i, a);
    else if (i instanceof O) i.target = i.target || a;
    else {
        var s = i,
            i = new O(r, a);
        Ea(i, s)
    }
    a = i.a = a, ac(a, r, !0, i), ac(a, r, !1, i)
};
P.prototype.g = function(t, e, a, n) {
    return $b(this), this.o.add(String(t), e, !1, a, n)
}, P.prototype.f = function(t, e, a, n) {
    return this.o.remove(String(t), e, a, n)
};
var ac = function(t, e, a, n) {
        if (e = t.o.a[String(e)]) {
            e = e.concat();
            for (var i = !0, r = 0; r < e.length; ++r) {
                var s = e[r];
                if (s && !s.s && s.C == a) {
                    var o = s.listener,
                        c = s.D || s.src;
                    s.B && Bb(t.o, s), i = !1 !== o.call(c, n) && i
                }
            }
        }
    },
    $b = function(t) {
        y(t.o, "Event target is not initialized. Did you call the superclass (goog.events.EventTarget) constructor?")
    },
    cc = function(t) {
        P.call(this), this.c = {}, this.b = {}, this.h = new Yb(this), this.i = t
    };
q(cc, P);
var dc = [F && !H("11") ? "readystatechange" : "load", "abort", "error"],
    ec = function(t) {
        var e = t.c;
        xa(Ba(e), function(t) {
            var a = e[t];
            if (a && (delete e[t], !this.a)) {
                var n;
                this.i ? (n = this.i, n = (n ? new Xb(Wb(n)) : ha || (ha = new Xb)).a("IMG")) : n = new Image, a.W && (n.crossOrigin = a.W), this.h.g(n, dc, this.j), this.b[t] = n, n.id = t, n.src = a.src
            }
        }, t)
    };
cc.prototype.j = function(t) {
    var e = t.a;
    if (e) {
        if ("readystatechange" == t.type) {
            if ("complete" != e.readyState) return;
            t.type = "load"
        }
        "undefined" == typeof e.naturalWidth && ("load" == t.type ? (e.naturalWidth = e.width, e.naturalHeight = e.height) : (e.naturalWidth = 0, e.naturalHeight = 0)), bc(this, {
            type: t.type,
            target: e
        }), !this.a && (t = e.id, delete this.c[t], e = this.b[t]) && (delete this.b[t], this.h.f(e, dc, this.j), Ca(this.b) && Ca(this.c) && bc(this, "complete"))
    }
};
var fc = /^(?:([^:\/?#.]+):)?(?:\/\/(?:([^\/?#]*)@)?([^\/#?]*?)(?::([0-9]+))?(?=[\/#?]|$))?([^?#]+)?(?:\?([^#]*))?(?:#(.*))?$/,
    hc = function(t) {
        if (gc) {
            gc = !1;
            var e = k.location;
            if (e) {
                var a = e.href;
                if (a && (a = (a = hc(a)[3] || null) ? decodeURI(a) : a) && a != e.hostname) throw gc = !0, Error()
            }
        }
        return t.match(fc)
    },
    gc = Qa,
    ic = function(t, e) {
        for (var a = t.split("&"), n = 0; n < a.length; n++) {
            var i = a[n].indexOf("="),
                r = null,
                s = null;
            i >= 0 ? (r = a[n].substring(0, i), s = a[n].substring(i + 1)) : r = a[n], e(r, s ? decodeURIComponent(s.replace(/\+/g, " ")) : "")
        }
    },
    Q = function(t, e) {
        this.f = this.j = this.c = "", this.i = null, this.h = this.b = "", this.a = !1;
        var a;
        t instanceof Q ? (this.a = void 0 !== e ? e : t.a, jc(this, t.c), this.j = t.j, this.f = t.f, kc(this, t.i), this.b = t.b, lc(this, t.g.clone()), this.h = t.h) : t && (a = hc(String(t))) ? (this.a = !!e, jc(this, a[1] || "", !0), this.j = mc(a[2] || ""), this.f = mc(a[3] || "", !0), kc(this, a[4]), this.b = mc(a[5] || "", !0), lc(this, a[6] || "", !0), this.h = mc(a[7] || "")) : (this.a = !!e, this.g = new nc(null, 0, this.a))
    };
Q.prototype.toString = function() {
    var t = [],
        e = this.c;
    if (e && t.push(oc(e, pc, !0), ":"), e = this.f) {
        t.push("//");
        var a = this.j;
        a && t.push(oc(a, pc, !0), "@"), t.push(encodeURIComponent(String(e)).replace(/%25([0-9a-fA-F]{2})/g, "%$1")), e = this.i, null != e && t.push(":", String(e))
    }
    return (e = this.b) && (this.f && "/" != e.charAt(0) && t.push("/"), t.push(oc(e, "/" == e.charAt(0) ? qc : rc, !0))), (e = this.g.toString()) && t.push("?", e), (e = this.h) && t.push("#", oc(e, sc)), t.join("")
}, Q.prototype.clone = function() {
    return new Q(this)
};
var jc = function(t, e, a) {
        t.c = a ? mc(e, !0) : e, t.c && (t.c = t.c.replace(/:$/, ""))
    },
    kc = function(t, e) {
        if (e) {
            if (e = Number(e), isNaN(e) || 0 > e) throw Error("Bad port number " + e);
            t.i = e
        } else t.i = null
    },
    lc = function(t, e, a) {
        e instanceof nc ? (t.g = e, tc(t.g, t.a)) : (a || (e = oc(e, uc)), t.g = new nc(e, 0, t.a))
    },
    vc = function(t) {
        return t instanceof Q ? t.clone() : new Q(t, void 0)
    },
    mc = function(t, e) {
        return t ? e ? decodeURI(t) : decodeURIComponent(t) : ""
    },
    oc = function(t, e, a) {
        return n(t) ? (t = encodeURI(t).replace(e, wc), a && (t = t.replace(/%25([0-9a-fA-F]{2})/g, "%$1")), t) : null
    },
    wc = function(t) {
        return t = t.charCodeAt(0), "%" + (t >> 4 & 15).toString(16) + (15 & t).toString(16)
    },
    pc = /[#\/\?@]/g,
    rc = /[\#\?:]/g,
    qc = /[\#\?]/g,
    uc = /[\#\?@]/g,
    sc = /#/g,
    nc = function(t, e, a) {
        this.c = this.a = null, this.b = t || null, this.f = !!a
    },
    R = function(t) {
        t.a || (t.a = new B, t.c = 0, t.b && ic(t.b, function(e, a) {
            t.add(decodeURIComponent(e.replace(/\+/g, " ")), a)
        }))
    };
h = nc.prototype, h.add = function(t, e) {
    R(this), this.b = null, t = xc(this, t);
    var a = Ma(this.a, t);
    return a || Ka(this.a, t, a = []), a.push(e), this.c++, this
}, h.remove = function(t) {
    return R(this), t = xc(this, t), C(this.a.b, t) ? (this.b = null, this.c -= Ma(this.a, t).length, this.a.remove(t)) : !1
}, h.u = function() {
    R(this);
    for (var t = this.a.v(), e = this.a.u(), a = [], n = 0; n < e.length; n++)
        for (var i = t[n], r = 0; r < i.length; r++) a.push(e[n]);
    return a
}, h.v = function(t) {
    R(this);
    var e = [];
    if (n(t)) {
        var a = t;
        R(this), a = xc(this, a), C(this.a.b, a) && (e = ya(e, Ma(this.a, xc(this, t))))
    } else
        for (t = this.a.v(), a = 0; a < t.length; a++) e = ya(e, t[a]);
    return e
}, h.toString = function() {
    if (this.b) return this.b;
    if (!this.a) return "";
    for (var t = [], e = this.a.u(), a = 0; a < e.length; a++)
        for (var n = e[a], i = encodeURIComponent(String(n)), n = this.v(n), r = 0; r < n.length; r++) {
            var s = i;
            "" !== n[r] && (s += "=" + encodeURIComponent(String(n[r]))), t.push(s)
        }
    return this.b = t.join("&")
}, h.clone = function() {
    var t = new nc;
    return t.b = this.b, this.a && (t.a = this.a.clone(), t.c = this.c), t
};
var xc = function(t, e) {
        var a = String(e);
        return t.f && (a = a.toLowerCase()), a
    },
    tc = function(t, e) {
        e && !t.f && (R(t), t.b = null, t.a.forEach(function(t, e) {
            var a = e.toLowerCase();
            e != a && (this.remove(e), this.remove(a), 0 < t.length && (this.b = null, Ka(this.a, xc(this, a), za(t)), this.c += t.length))
        }, t)), t.f = e
    };
F && H(8);
var yc = {
        Ia: !0
    },
    zc = function() {
        throw Error("Do not instantiate directly")
    };
zc.prototype.N = null, zc.prototype.toString = function() {
    return this.a
};
var Ac = function(t) {
        if (null != t) switch (t.N) {
            case 1:
                return 1;
            case -1:
                return -1;
            case 0:
                return 0
        }
        return null
    },
    Bc = function() {
        zc.call(this)
    };
q(Bc, zc), Bc.prototype.ma = yc;
var T = function(t) {
        if (null != t && t.ma === yc) return y(t.constructor === Bc), t;
        if (t instanceof A) {
            var e, a = S;
            t instanceof A && t.constructor === A && t.c === Ga ? e = t.a : (va("expected object of type SafeHtml, got '" + t + "'"), e = "type_error:SafeHtml"), t = a(e, t.b)
        } else t = S(ra(String(String(t))), Ac(t));
        return t
    },
    S = function(t) {
        function e(t) {
            this.a = t
        }
        return e.prototype = t.prototype,
            function(t, a) {
                var n = new e(String(t));
                return void 0 !== a && (n.N = a), n
            }
    }(Bc);
! function(t) {
    function e(t) {
        this.a = t
    }
    return e.prototype = t.prototype,
        function(t, a) {
            var n = String(t);
            return n ? (n = new e(n), void 0 !== a && (n.N = a), n) : ""
        }
}(Bc);
var Cc = function(t) {
        var e = 4,
            a = !1;
        if (t = String(t), t.length <= e) return t;
        a && (e > 3 ? e -= 3 : a = !1);
        var n = t.charAt(e - 1);
        return (n = n >= 55296 && 56319 >= n) && (n = t.charAt(e), n = n >= 56320 && 57343 >= n), n && --e, t = t.substring(0, e), a && (t += "..."), t
    },
    Dc = function(t) {
        var e = "";
        return e = t.media && t.media.metadata && t.media.metadata.title ? e + ("Now Casting: " + t.media.metadata.title) : t.media ? e + "Now Casting" : e + "Ready To Cast"
    },
    Y = function(t) {
        if (this.a = Ec, pb || (pb = new mb), k.location && -1 != k.location.href.indexOf("Debug=true")) {
            var e = pb;
            if (1 != e.b) {
                hb();
                var a = M,
                    n = e.h;
                a.a || (a.a = []), a.a.push(n), e.b = !0
            }
        }
        /[&?]Debug=true(&|$)/.test(window.location.search) && (cast.player.api.setLoggerLevel(cast.player.api.LoggerLevel.DEBUG), cast.receiver.logger.setLevelValue(cast.receiver.LoggerLevel.DEBUG)), this.l = t, this.l.className = "gcpa", this.l.innerHTML = S('<div class="gcpb"><video mediagroup="megacast" id="videoContent"></video><audio mediagroup="megacast" id="audioContent"></audio><div class="logo"></div><div class="gcpp"></div><div class="splash"></div><div class="watermark"></div><div class="gcpc"></div><div class="gcpd"><div class="gcpf"><div class="gcpq"></div><div class="gcpe"></div></div><div class="gcpi"><span class="gcpj"></span><span class="gcpn"></span><span class="gcpo"></span><div class="gcpk"><div class="gcpl progressBar"></div><div class="gcpm"></div></div></div></div></div>'), Fc(this, "unknown", !1), this.J = 0, this.g("launching", !1), this.A = U(this, "gcpb"), this.R = U(this, "gcpl"), this.S = U(this, "gcpm"), this.ba = U(this, "gcpn"), this.V = U(this, "gcpo"), this.G = this.pa.bind(this), this.i = this.h = this.f = null, this.L = !1, this.w = null, this.K = this.M = !1, this.b = this.l.querySelector("video"), this.b.addEventListener("playing", this.wa.bind(this), !1), this.b.addEventListener("pause", this.va.bind(this), !1), this.b.addEventListener("ended", this.ra.bind(this), !1), this.b.addEventListener("abort", this.oa.bind(this), !1), this.b.addEventListener("timeupdate", this.ya.bind(this), !1), this.b.addEventListener("seeking", this.Ba.bind(this), !1), this.b.addEventListener("seeked", this.Aa.bind(this), !1), this.H = {}, this.j = cast.receiver.CastReceiverManager.getInstance(), this.j.onReady = this.za.bind(this), this.j.onSenderConnected = this.Ca.bind(this), this.j.onSenderDisconnected = this.Da.bind(this), this.j.onVisibilityChanged = this.Fa.bind(this), this.j.setApplicationState(Dc({}).toString()), this.c = new cast.receiver.MediaManager(this.b), this.ja = this.c.onLoad.bind(this.c), this.c.onLoad = this.ta.bind(this), this.ga = this.c.onEditTracksInfo.bind(this.c), this.c.onEditTracksInfo = this.qa.bind(this), this.ka = this.c.onMetadataLoaded.bind(this.c), this.c.onMetadataLoaded = this.ua.bind(this), this.Y = this.c.onStop.bind(this.c), this.c.onStop = this.Ea.bind(this), this.ia = this.c.onLoadMetadataError.bind(this.c), this.c.onLoadMetadataError = this.F.bind(this), this.ha = this.c.onError.bind(this.c), this.c.onError = this.sa.bind(this), this.c.customizedStatusCallback = this.na.bind(this), this.c.onPreload = this.xa.bind(this)
    },
    Gc = ["castplayer", "CastPlayer"],
    Z = k;
Gc[0] in Z || !Z.execScript || Z.execScript("var " + Gc[0]);
for (var Hc; Gc.length && (Hc = Gc.shift());) Gc.length || void 0 === Y ? Z = Z[Hc] ? Z[Hc] : Z[Hc] = {} : Z[Hc] = Y;
var Ec;
Ec = ib("castplayer.CastPlayer");
var Ic = {
    idle: 12e5,
    loading: 3e5,
    paused: 12e5
};
Y.prototype.da = function() {
    return this.b
}, Y.prototype.getMediaElement = Y.prototype.da, Y.prototype.ea = function() {
    return this.c
}, Y.prototype.getMediaManager = Y.prototype.ea, Y.prototype.fa = function() {
    return this.f
}, Y.prototype.getPlayer = Y.prototype.fa, Y.prototype.aa = function() {
    this.j.start()
}, Y.prototype.start = Y.prototype.aa, Y.prototype.P = function(t) {
    var e;
    return (e = !cast.player.api.Player.prototype.preload) || (e = null == Jc(t || {})), e ? !1 : (this.h && (this.h.unload(), this.h = null), Kc(this, t))
}, Y.prototype.preload = Y.prototype.P;
var Kc = function(t, e) {
    var a = e.contentId,
        n = Jc(e);
    return n ? (a = new cast.player.api.Host({
        url: a,
        mediaElement: t.b
    }), t.h = new cast.player.api.Player(a), a.onError = function() {
        t.h.unload(), t.h = null, J(t.a, "Error during preload")
    }, t.h.preload(n(a)), !0) : (J(t.a, "No protocol found for preload"), !1)
};
Y.prototype.O = function(t) {
    clearTimeout(this.U);
    var e = this,
        a = t.message.media || {},
        n = a.contentType,
        i = Lc(a),
        r = a.streamType == cast.receiver.media.StreamType.LIVE;
    if (a.contentId)
        if ("unknown" == i) J(this.a, "Load failed: unknown content type: " + n), e.F(t);
        else {
            var s = null;
            switch (Mc(e), Fc(e, i, r), i) {
                case "audio":
                    Nc(e, t);
                    break;
                case "video":
                    Oc(e, t);
                    break;
                case "image":
                    s = e.$.bind(e, t);
                    break;
                default:
                    va("Unknown player type")
            }
            e.M = !1, e.K = !1, Pc(e, a), Qc(a, function() {
                Rc(e.A, function() {
                    e.g("loading", !1), s ? s() : (e.M = !0, Sc(e, t), e.L && (Tc(e), e.L = !1))
                })
            })
        } else J(this.a, "Load failed: no content"), e.F(t)
}, Y.prototype.load = Y.prototype.O;
var Sc = function(t, e) {
        t.M ? t.K ? (t.ka(e), K(t.a, "Sent load response, player is ready and metadata loaded")) : K(t.a, "Deferring load response, loadedmetadata event not received") : K(t.a, "Deferring load response, player not ready")
    },
    Mc = function(t) {
        t.b.style.backgroundImage = "none", t.f && (t.f.unload(), t.f = null), t.i = null
    },
    Pc = function(t, e) {
        var a, n = U(t, "gcpe"),
            i = e.metadata || {};
        a = S, i = i ? S(1 == i.metadataType ? (i.title ? '<div class="gcpg">' + T(i.title) + "</div>" : "") + (i.subtitle || i.studio || i.releaseDate ? '<div class="gcph">' + (i.subtitle ? "<div><span>" + T(i.subtitle) + "</span></div>" : "") + (i.studio || i.releaseDate ? "<div>" + (i.studio ? "<span>" + T(i.studio) + "</span>" : "") + (i.releaseDate ? "<span>" + T(Cc(i.releaseDate)) + "</span>" : "") + "</div>" : "") + "</div>" : "") : 2 == i.metadataType ? (i.title ? '<div class="gcpg">' + T(i.title) + "</div>" : "") + (i.season || i.episode || i.seriesTitle || i.originalAirdate ? '<div class="gcph">' + (i.season || i.episode || i.originalAirdate ? "<div>" + (i.season || i.episode ? "<span>" + (i.season && i.episode ? "Season " + T(i.season) + ", Episode " + T(i.episode) : i.season ? "Season " + T(i.season) : i.episode ? "Episode " + T(i.episode) : "") + "</span>" : "") + (i.originalAirdate ? "<span>" + T(Cc(i.originalAirdate)) + "</span>" : "") + "</div>" : "") + (i.seriesTitle ? "<div><span>" + T(i.seriesTitle) + "</span></div>" : "") + "</div>" : "") : 3 == i.metadataType ? (i.title ? '<div class="gcpg">' + T(i.title) + "</div>" : "") + (i.artist || i.composer || i.albumName || i.albumArtist || i.releaseDate ? '<div class="gcph">' + (i.artist ? "<div>" + (i.albumArtist ? "<span>" + T(i.albumArtist) + "</span>" : i.artist ? "<span>" + T(i.artist) + "</span>" : i.composer ? "<span>" + T(i.composer) + "</span>" : "") + "</div>" : "") + (i.albumName || i.releaseDate ? "<div>" + (i.albumName ? "<span>" + T(i.albumName) + "</span>" : "") + (i.releaseDate ? "<span>" + T(Cc(i.releaseDate)) + "</span>" : "") + "</div>" : "") + "</div>" : "") : (i.title ? '<div class="gcpg">' + T(i.title) + "</div>" : "") + (i.subtitle ? '<div class="gcph">' + T(i.subtitle) + "</div>" : "")) : "", a = a(i), n.innerHTML = a, n = Uc(e), a = U(t, "gcpq"), n ? (a.style.content = 'url("' + n.replace(/"/g, '\\"') + '")', a.classList.remove("placeholderImage")) : (a.classList.add("placeholderImage"), a.style.removeProperty("content"))
    },
    Vc = function(t, e) {
        var a = e.message.autoplay;
        e.message.autoplay = !1, t.b.autoplay = !1, t.L = void 0 == a ? !0 : a
    },
    Nc = function(t, e) {
        y("audio" == t.I, "loadAudio called when type != AUDIO"), Vc(t, e), Wc(t, e)
    };
Y.prototype.$ = function(t) {
    y("image" == this.I, "loadImage called when type != IMAGE");
    var e = this,
        a = e.l.ownerDocument.createElement("img");
    a.onerror = e.F.bind(e, t), a.onload = function() {
        e.g("paused", !1), e.b.style.backgroundImage = 'url("' + a.src.replace(/"/g, '\\"') + '")', e.c.sendLoadComplete()
    }, a.src = t.message.media.contentId
};
var Oc = function(t, e) {
        y("video" == t.I, "loadVideo called when type != VIDEO");
        var a = e.message.media.contentId,
            n = Jc(e.message.media);
        if (Vc(t, e), n) {
            t.b.removeEventListener("stalled", t.G), t.b.removeEventListener("waiting", t.G);
            var i = function() {
                t.f && (Mc(t), t.b.dispatchEvent(new Event("error")))
            };
            !t.h || t.h.getHost && t.h.getHost().url != a ? (t.h && (t.h.unload(), t.h = null), K(t.a, "Regular video load"), a = new cast.player.api.Host({
                url: a,
                mediaElement: t.b
            }), a.onError = i, t.f = new cast.player.api.Player(a), t.f.load(n(a))) : (K(t.a, "Preloaded video load"), t.f = t.h, t.h = null, t.f.getHost().onError = i, t.f.load())
        } else t.b.addEventListener("stalled", t.G, !1), t.b.addEventListener("waiting", t.G, !1);
        Xc(t, e, !!n)
    },
    Xc = function(t, e, a) {
        a ? e.message && e.message.media && e.message.media.tracks && 0 != e.message.media.tracks.length && t.c.loadTracksInfo({
            tracks: e.message.media.tracks,
            activeTrackIds: e.message.activeTrackIds,
            textTrackStyle: e.message.media.textTrackStyle
        }) : Wc(t, e)
    },
    Zc = function(t, e, a) {
        if (0 != e.length)
            for (var n = 0; n < a.length; n++)
                if (0 <= wa(e, a[n].trackId) && Yc(a[n], "ttml", "application/ttml+xml")) {
                    if (!t.f) {
                        var i = new cast.player.api.Host({
                            url: "",
                            mediaElement: t.b
                        });
                        t.f = new cast.player.api.Player(i)
                    }
                    t.f.enableCaptions(!0, cast.player.api.CaptionsType.TTML, a[n].trackContentId)
                }
    },
    Yc = function(e, a, n) {
        if (!e) return !1;
        var i = e.trackContentId;
        return e = e.trackContentType, i && w(i, a) || e && 0 == t(e, n) ? !0 : !1
    },
    Wc = function(t, e) {
        t.ja(new cast.receiver.MediaManager.Event(cast.receiver.MediaManager.EventType.LOAD, e.message, e.senderId))
    },
    $c = function(t, e) {
        clearTimeout(t.U), e && (t.U = setTimeout(function() {
            K(t.a, "Suicide timer expired"), t.j.stop()
        }, e))
    },
    Fc = function(t, e, a) {
        K(t.a, "type changed: " + e), t.I = e, t.l.setAttribute("type", e), t.l.setAttribute("live", a.toString());
        var n = U(t, "gcpd"),
            i = U(t, "watermark");
        clearInterval(t.Z), "audio" != e ? (n.removeAttribute("style"), i.removeAttribute("style")) : t.Z = setInterval(function() {
            n.style.marginBottom = Math.round(100 * Math.random()) + "px", n.style.marginLeft = Math.round(600 * Math.random()) + "px", i.style.marginBottom = Math.round(50 * Math.random()) + "px", i.style.marginRight = Math.round(50 * Math.random()) + "px"
        }, 15e3)
    };
Y.prototype.g = function(t, e, a) {
    clearTimeout(this.la);
    var n = this;
    if (n.J = Date.now(), a) n.la = setTimeout(n.g.bind(n, t, e), a);
    else if (e) {
        var i = n.J;
        Rc(n.A, function() {
            i < n.J ? K(n.a, "discarded obsolete deferred state(" + t + ").") : n.g(t, !1)
        })
    } else K(n.a, "state changed: " + t), n.m = t, n.l.setAttribute("state", t), ad(n), $c(n, Ic[t])
};
var ad = function(t) {
    if (t.c) {
        var e = "idle" == t.m ? null : t.c.getMediaInformation(),
            e = Dc({
                media: e
            }).toString();
        t.ca != e && (t.ca = e, t.j.setApplicationState(e))
    }
};
h = Y.prototype, h.za = function() {
    K(this.a, "onReady");
    var t = this.j.getApplicationData(),
        e = U(this, "logo"),
        a = window.getComputedStyle(e, null);
    "none" == a.backgroundImage && (e.textContent = t.name), e = U(this, "splash"), "none" != window.getComputedStyle(e, null).backgroundImage ? this.g("idle", !0, 3e3) : (this.g("idle", !1), "none" == a.backgroundImage ? e.textContent = t.name : e.classList.add("logo"))
}, h.Ca = function(t) {
    K(this.a, "onSenderConnected");
    var e = this.j.getSender(t.senderId);
    y(null !== e), this.H[t.senderId] = e
}, h.Da = function(t) {
    K(this.a, "onSenderDisconnected");
    var e = this.H[t.senderId].userAgent.split(",");
    delete this.H[t.senderId];
    var a = 0 == Ba(this.H).length;
    t = t.reason == cast.receiver.system.DisconnectReason.REQUESTED_BY_SENDER, e = "iOS CastSDK" == e[0] && 0 >= ta(e[1], "2.2.0"), a && t && !e && this.j.stop()
}, h.sa = function(t) {
    K(this.a, "onError");
    var e = this;
    Rc(this.A, function() {
        e.g("idle", !0), e.ha(t)
    })
}, h.pa = function() {
    K(this.a, "onBuffering[readyState=" + this.b.readyState + "]"), "playing" == this.m && this.b.readyState < HTMLMediaElement.HAVE_ENOUGH_DATA && this.g("buffering", !1)
}, h.wa = function() {
    K(this.a, "onPlaying"), bd(this, "media is already playing");
    var t = "audio" == this.I;

	if(vid == null) {
    	vid = document.getElementById('videoContent');
    }
    if(aud == null) {
    	aud = document.getElementById('audioContent');
    }
    if(vid.muted === true) {
        
            var vct = vid.currentTime;
            aud.currentTime = vid.currentTime;
            aud.play();


                
                
                var intervalAF = window.setInterval(function () {
                
                    if(vid.paused === false && aud.paused === false && vid.currentTime > (vct + 1.0)) {
                        var diff = vid.currentTime - aud.currentTime;
                        if (Math.abs(diff) > 0.025) {
                            console.log('working... ', diff);
                            aud.currentTime = vid.currentTime + diff;   
                        }
                        else {
                            console.log('stopped... ', diff);
                            clearInterval(intervalAF);
                        }
                    }
                }, 100);
        
    }
    else {
        aud.pause();
    }

    this.g("playing", "loading" == this.m && !t)
}, h.va = function() {
    K(this.a, "onPause"), bd(this, "media is paused");
    if(aud == null || aud === null) {
    	aud = document.getElementById('audioContent');
    }
    aud.pause();
    aud.currentTime = 0;
    aud.removeAttribute("src");

    var t = "idle" == this.m,
        e = this.b.currentTime == this.b.duration;
    this.f && this.f.getState().underflow ? (this.g("buffering", !1), this.c.broadcastStatus(!1)) : t || e || this.g("paused", !1), cd(this)
}, h.na = function(t) {
    return t.playerState == cast.receiver.media.PlayerState.PAUSED && "buffering" == this.m && (t.playerState = cast.receiver.media.PlayerState.BUFFERING), t
}, h.Ea = function(t) {
    K(this.a, "onStop"), bd(this, "media is stopped");
    if(aud == null) {
    	aud = document.getElementById('audioContent');
    }
    if(vid == null) {
    	vid = document.getElementById('videoContent');
    }
    aud.pause();
    aud.currentTime = 0;
    aud.removeAttribute("src");
    aud.muted = true;
    vid.muted = false;
    audioTrack = null;
    var e = this;
    Rc(e.A, function() {
        e.g("idle", !1), e.Y(t)
    })
}, h.ra = function() {
    K(this.a, "onEnded"), this.g("idle", !0)
}, h.oa = function() {
    K(this.a, "onAbort"), this.g("idle", !0)
}, h.ya = function() {
    "buffering" == this.m && this.g("playing", !1), cd(this)
};
var cd = function(t) {
    var e = t.b.currentTime;
    t.ba.textContent = dd(e);
    var a = t.b.duration;
    isFinite(a) && (e = (100 * e / a).toFixed(2) + "%", t.R.style.width = e, t.S.style.left = e)
};
h = Y.prototype, h.Ba = function() {
    K(this.a, "onSeekStart"), clearTimeout(this.T), this.l.classList.add("gcpr");
    if(typeof(aud.src) === "string") {
    	aud.pause();
    }
}, h.Aa = function() {
    K(this.a, "onSeekEnd"), clearTimeout(this.T), this.T = ed(this.l);
}, h.Fa = function(t) {
    K(this.a, "onVisibilityChanged"), t.isVisible || (this.b.pause(), this.c.broadcastStatus(!1))
}, h.xa = function(t) {
    return K(this.a, "onPreload"), this.P(t.data.media)
}, h.ta = function(t) {
    K(this.a, "onLoad"), bd(this, "new media is loaded"), this.O(new cast.receiver.MediaManager.LoadInfo(t.data, t.senderId))
}, h.qa = function(t) {
    if (K(this.a, "onEditTracksInfo"), this.ga(t), t.data && t.data.activeTrackIds && this.i) {
        var e = this.c.getMediaInformation() || {},
            a = this.i; 
        if ("ttml" == a) this.f && this.f.enableCaptions(!1, cast.player.api.CaptionsType.TTML), Zc(this, t.data.activeTrackIds, e.tracks || []);
        else if ("embedded" == a) {
            this.f.enableCaptions(!1), t = t.data.activeTrackIds;
            for (var e = this.f.getStreamingProtocol(), a = e.getStreamCount(), n = 0; a > n; n++) {
                var i = 0 <= wa(t, n + 1),
                    r = e.isStreamEnabled(n);
                i && !r ? e.enableStream(n, !0) : !i && r && e.enableStream(n, !1)
            }
            this.f.enableCaptions(!0)
        }
    }
}, h.ua = function(t) {
    K(this.a, "onMetadataLoaded"), K(this.a, "onLoadSuccess");
    var e = this.b.duration;
    if (isFinite(e) ? this.V.textContent = dd(e) : (this.V.textContent = "", this.R.style.width = "100%", this.S.style.left = "100%"), t.message && t.message.media && t.message.media.tracks)
        for (e = 0; e < t.message.media.tracks.length; e++) {
     		//console.log("onMetadataLoaded/onLoadSuccess: " + t.message.media.tracks[e].type);
            var a = this.i;
            if (t.message.media.tracks[e].type == cast.receiver.media.TrackType.AUDIO) {
            	audioTrack = t.message.media.tracks[e];
            	aud = document.getElementById("audioContent");
            	vid = document.getElementById("videoContent");
            	aud.src = t.message.media.tracks[e].trackContentId;
            	aud.type = t.message.media.tracks[e].trackContentType;

                    //aud.pause();
                    //vid.pause();

                    //aud.mediaGroup = "mcast";
                    //vid.mediaGroup = "mcast";

                    //aud.mediagroup = "mcast";
                    //vid.mediagroup = "mcast";

                    //aud.setAttribute("mediagroup", "mcast");
                    //vid.setAttribute("mediagroup", "mcast");

                    vid.currentTime = 0.0;
                    aud.currentTime = 0.0;

                    //aud.play();
                    //vid.play();


            	//aud.muted = false;
            	vid.muted = true;
            }
            if (t.message.media.tracks[e].type == cast.receiver.media.TrackType.TEXT) {
            	//console.log("JESTE UCITAO SUBTITLE: " + t.message.media[e].trackContentId);
                if (Yc(t.message.media.tracks[e], "ttml", "application/ttml+xml")) this.i = "ttml";
                else {
                    if (!Yc(t.message.media.tracks[e], "vtt", "text/vtt")) {
                        J(this.a, "Unsupported side loaded text track types"), this.i = "unsupported";
                        continue
                    }
                    this.i = "vtt"
                }
                if (a && a != this.i) {
                    J(this.a, "Load has inconsistent text track types"), this.i = "unsupported";
                    continue
                }
            }
        }
    if ("ttml" == this.i && t.message && t.message.activeTrackIds && t.message.media && t.message.media.tracks) Zc(this, t.message.activeTrackIds, t.message.media.tracks);
    else if (!this.i && t.message && t.message.media) {
        var n;
        if (e = this.f ? this.f.getStreamingProtocol() : null) {
            for (var a = e.getStreamCount(), i = [], r = [], s = 0; a > s; s++) {
                var o = s + 1;
                e.isStreamEnabled(s) && i.push(o);
                var c, l = e.getStreamInfo(s),
                    h = l.mimeType;
                (c = u(h, "text")) || (c = "application/ttml+xml" == h.toLowerCase()), c ? n = new cast.receiver.media.Track(o, cast.receiver.media.TrackType.TEXT) : u(h, "video") ? n = new cast.receiver.media.Track(o, cast.receiver.media.TrackType.VIDEO) : u(h, "audio") && (n = new cast.receiver.media.Track(o, cast.receiver.media.TrackType.AUDIO)), n && (n.name = l.name, n.language = l.language, n.trackContentType = l.mimeType, r.push(n))
            }
            n = 0 === r.length ? null : {
                tracks: r,
                activeTrackIds: i
            }
        } else n = null;
        n && (this.i = "embedded", n.textTrackStyle = t.message.media.textTrackStyle, this.c.loadTracksInfo(n))
    }
    this.K = !0, Sc(this, t)
}, h.F = function(t) {
    K(this.a, "onLoadError");
    var e = this;
    Rc(this.A, function() {
        e.g("idle", !0), e.ia(t)
    })
};
var bd = function(t, e) {
        t.w && (K(t.a, "Cancelled deferred playback: " + e), clearTimeout(t.w), t.w = null)
    },
    Tc = function(t) {
        K(t.a, "Defering playback for 2000 ms"), t.w = setTimeout(function() {
            t.w = null, t.f ? (K(t.a, "Playing when enough data"), t.f.playWhenHaveEnoughData()) : (K(t.a, "Playing"), t.b.play())
        }, 2e3)
    },
    U = function(t, e) {
        var a = t.l.querySelector("." + e);
        return a ? a : void va("Cannot find element with class: " + e)
    },
    Uc = function(t) {
        return t = (t.metadata || {}).images || [], t[0] && t[0].url
    },
    Jc = function(e) {
        var a = e.contentType || "";
        return e = vc(e.contentId).b || "", w(e, ".m3u8") || 0 == t(a, "application/x-mpegurl") || 0 == t(a, "application/vnd.apple.mpegurl") ? cast.player.api.CreateHlsStreamingProtocol : w(e, ".mpd") || 0 == t(a, "application/dash+xml") ? cast.player.api.CreateDashStreamingProtocol : -1 != e.indexOf(".ism") || 0 == t(a, "application/vnd.ms-sstr+xml") ? cast.player.api.CreateSmoothStreamingProtocol : null
    },
    Lc = function(e) {
        var a = e.contentType || "";
        return e = vc(e.contentId || "").b || "", u(a, "audio/") ? "audio" : u(a, "image/") ? "image" : u(a, "video/") ? "video" : 0 == t(a, "application/x-mpegurl") ? "video" : 0 == t(a, "application/vnd.apple.mpegurl") ? "video" : 0 == t(a, "application/dash+xml") ? "video" : 0 == t(a, "application/vnd.ms-sstr+xml") ? "video" : w(e, ".mp3") ? "audio" : w(e, ".oga") ? "audio" : w(e, ".wav") ? "audio" : w(e, ".jpg") ? "image" : w(e, ".gif") ? "image" : w(e, ".png") ? "image" : w(e, ".mp4") ? "video" : w(e, ".ogv") ? "video" : w(e, ".webm") ? "video" : w(e, ".m3u8") ? "video" : w(e, ".mpd") ? "video" : -1 != e.indexOf(".ism") ? "video" : "unknown"
    },
    dd = function(t) {
        t = Math.floor(t);
        var e = Math.floor(t / 3600),
            a = Math.floor(t / 60) % 60;
        return t %= 60, e ? x(e) + ":" + x(a) + ":" + x(t) : x(a) + ":" + x(t)
    },
    ed = function(t) {
        return t.classList.add("gcpr"), setTimeout(function() {
            t.classList.remove("gcpr")
        }, 3e3)
    },
    Rc = function(t, e) {
        fd(t, function() {
            e(), gd(t, "", void 0)
        })
    },
    Qc = function(t, e) {
        var a = [],
            i = Uc(t);
        i && a.push(i);
        var i = t.contentId,
            r = Lc(t);
        if (i && "image" == r && a.push(i), 0 == a.length) e();
        else {
            for (i = new cc, Gb(i, "complete", e), r = 0; r < a.length; r++) {
                var s = a[r];
                (s = n(s) ? s : s.src) && (i.c[a[r]] = {
                    src: s,
                    W: null
                })
            }
            ec(i)
        }
    },
    fd = function(t, e) {
        gd(t, 0, e)
    },
    gd = function(t, e, a) {
        var n = Date.now(),
            i = function() {
                K(Ec, "Transition end [" + n + "]"), t.style.webkitTransition = "", t.removeEventListener("webkitTransitionEnd", i, !1), a && a()
            };
        K(Ec, "Transition start [" + n + "] Opacity: " + e + " 0.75s"), t.addEventListener("webkitTransitionEnd", i, !1), t.style.webkitTransition = "opacity 0.75s", t.style.opacity = e
    };
location.search.slice(1).split("&").forEach(function(t) {
    if (t = t.split("="), "skin" == t[0]) {
        var e = document.createElement("link");
        e.setAttribute("rel", "stylesheet"), e.setAttribute("type", "text/css"), e.setAttribute("href", unescape(t[1])), document.head.appendChild(e)
    }
});