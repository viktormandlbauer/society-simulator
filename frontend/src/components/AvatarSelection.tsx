'use client';

import { useEffect, useMemo, useState } from 'react';

export type AvatarOption = {
  id: string;
  label: string;
  iconClass: string;
};

export const DEFAULT_AVATARS: AvatarOption[] = [
  { id: 'mario', label: 'Mario', iconClass: 'nes-mario' },
  { id: 'ash', label: 'Ash', iconClass: 'nes-ash' },
  { id: 'bulbasaur', label: 'Bulbasaur', iconClass: 'nes-bulbasaur' },
  { id: 'charmander', label: 'Charmander', iconClass: 'nes-charmander' },
  { id: 'squirtle', label: 'Squirtle', iconClass: 'nes-squirtle' },
  { id: 'pokeball', label: 'Poké Ball', iconClass: 'nes-pokeball' },
  { id: 'kirby', label: 'Kirby', iconClass: 'nes-kirby' },
  { id: 'octocat', label: 'Octocat', iconClass: 'nes-octocat' },
];

type AvatarSelectionProps = {
  avatars?: AvatarOption[];
  selectedId?: string | null;
  onChange?: (avatar: AvatarOption) => void;
  onConfirm?: (avatar: AvatarOption) => void;
  label?: string;
  className?: string;
  confirmLabel?: string;
};

export function AvatarSelection({
  avatars,
  selectedId,
  onChange,
  onConfirm,
  label = 'Choose your avatar',
  className,
  confirmLabel = 'Confirm avatar',
}: AvatarSelectionProps) {
  const options = useMemo(
    () => (avatars && avatars.length > 0 ? avatars : DEFAULT_AVATARS),
    [avatars],
  );

  const [internalId, setInternalId] = useState<string | null>(() => {
    if (selectedId != null && selectedId.length > 0) return selectedId;
    return options[0]?.id ?? null;
  });

  useEffect(() => {
    if (!selectedId) return;
    setInternalId(selectedId);
  }, [selectedId]);

  useEffect(() => {
    if (!internalId && options[0]) {
      setInternalId(options[0].id);
    }
  }, [internalId, options]);

  if (options.length === 0) {
    return null;
  }

  const activeId = internalId ?? options[0].id;
  const activeIndex = Math.max(
    0,
    options.findIndex((option) => option.id === activeId),
  );
  const activeOption = options[activeIndex] ?? options[0];
  const isConfirmed = selectedId === activeOption.id;

  const mergeClasses = (...parts: (string | undefined | null | false)[]) =>
    parts.filter(Boolean).join(' ');

  const setActiveIndex = (index: number) => {
    const wrappedIndex = (index + options.length) % options.length;
    const next = options[wrappedIndex];
    if (!next) return;
    setInternalId(next.id);
    onChange?.(next);
  };

  const showPrev = () => setActiveIndex(activeIndex - 1);
  const showNext = () => setActiveIndex(activeIndex + 1);

  const containerClasses = mergeClasses(
    'nes-container',
    'with-title',
    'flex',
    'flex-col',
    'items-center',
    'w-full',
    'max-w-sm',
    'mx-auto',
    className,
  );

  return (
    <section className={containerClasses}>
      <p className="title">{label}</p>

      <div className="flex items-center gap-4">
        <button
          type="button"
          className="nes-btn"
          onClick={showPrev}
          aria-label="Previous avatar"
        >
          &lt;
        </button>

        <div className="flex flex-col items-center gap-3">
          <span
            className={mergeClasses(
              'flex',
              'h-24',
              'w-24',
              'items-center',
              'justify-center',
              'border-4',
              'border-solid',
              'border-black',
              'bg-white',
            )}
          >
            <i className={activeOption.iconClass} aria-hidden="true" />
          </span>
          <span className="text-xs uppercase tracking-wide">
            {activeOption.label}
          </span>
          {selectedId && (
            <span className="text-[0.65rem] text-gray-500">
              {isConfirmed ? 'Confirmed' : 'Not confirmed'}
            </span>
          )}
          <button
            type="button"
            className={mergeClasses(
              'nes-btn',
              'is-success',
              'mt-4',
              isConfirmed && 'is-disabled',
            )}
            onClick={() => {
              if (isConfirmed) return;
              onConfirm?.(activeOption);
            }}
            disabled={isConfirmed}
          >
            {confirmLabel}
          </button>
        </div>

        <button
          type="button"
          className="nes-btn"
          onClick={showNext}
          aria-label="Next avatar"
        >
          &gt;
        </button>
      </div>
    </section>
  );
}

export default AvatarSelection;
