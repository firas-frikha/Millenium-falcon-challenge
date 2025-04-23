#!/usr/bin/env bash

set -Eeuox pipefail

_is_sourced() {
	[ "${#FUNCNAME[@]}" -ge 2 ] \
		&& [ "${FUNCNAME[0]}" = '_is_sourced' ] \
		&& [ "${FUNCNAME[1]}" = 'source' ]
}

_main() {
	if [ "${1:0:1}" = '-' ]; then
		set -- millenniumfalconbackend "$@"
	fi

	if [ "$1" = 'millenniumfalconbackend' -a "$(id -u)" = '0' ]; then
		exec setpriv --reuid=millenniumfalconbackend --regid=millenniumfalconbackend --init-groups "$BASH_SOURCE" "$@"
	fi

	exec "$@"
}

if ! _is_sourced; then
	_main "$@"
fi
