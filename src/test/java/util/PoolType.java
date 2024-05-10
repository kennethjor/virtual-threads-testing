package util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PoolType {
	PLATFORM(false),
	VIRTUAL(true);

	private final boolean virtual;
}
